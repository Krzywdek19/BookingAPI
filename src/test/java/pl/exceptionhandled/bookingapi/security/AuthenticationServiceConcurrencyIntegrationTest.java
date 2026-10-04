package pl.exceptionhandled.bookingapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import pl.exceptionhandled.bookingapi.common.exception.EmailIsAlreadyTakenException;
import pl.exceptionhandled.bookingapi.security.dto.RegisterRequest;
import pl.exceptionhandled.bookingapi.user.UserRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class AuthenticationServiceConcurrencyIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18");

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    private static final int THREADS_QUANTITY = 20;

    @Test
    void shouldRejectConcurrentRegistrationWithSameEmail()
            throws InterruptedException {

        var request = new RegisterRequest(
                "test@example.com",
                "password"
        );

        var successCount = new AtomicInteger();
        var emailTakenCount = new AtomicInteger();
        var unexpectedFailures = new AtomicInteger();

        var readyLatch = new CountDownLatch(THREADS_QUANTITY);
        var startLatch = new CountDownLatch(1);
        var finishedLatch = new CountDownLatch(THREADS_QUANTITY);

        Runnable task = () -> {
            try {
                readyLatch.countDown();

                startLatch.await();

                authenticationService.registerUser(request);

                successCount.incrementAndGet();

            } catch (EmailIsAlreadyTakenException e) {
                emailTakenCount.incrementAndGet();

            } catch (Exception e) {
                unexpectedFailures.incrementAndGet();

            } finally {
                finishedLatch.countDown();
            }
        };

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < THREADS_QUANTITY; i++) {
                executor.submit(task);
            }

            readyLatch.await();

            startLatch.countDown();

            finishedLatch.await();
        }

        assertEquals(1, successCount.get());
        assertEquals(THREADS_QUANTITY - 1, emailTakenCount.get());
        assertEquals(0, unexpectedFailures.get());

        assertEquals(
                1,
                userRepository.findAll().size()
        );
    }
}