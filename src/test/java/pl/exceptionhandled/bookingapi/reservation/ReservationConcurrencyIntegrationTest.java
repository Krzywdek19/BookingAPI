package pl.exceptionhandled.bookingapi.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import pl.exceptionhandled.bookingapi.common.exception.ReservationConflictException;
import pl.exceptionhandled.bookingapi.event.Event;
import pl.exceptionhandled.bookingapi.event.EventRepository;
import pl.exceptionhandled.bookingapi.seat.Seat;
import pl.exceptionhandled.bookingapi.seat.SeatRepository;
import pl.exceptionhandled.bookingapi.user.Role;
import pl.exceptionhandled.bookingapi.user.User;
import pl.exceptionhandled.bookingapi.user.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class ReservationConcurrencyIntegrationTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer("postgres:18");

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SeatRepository seatRepository;

    @Test
    void shouldAllowOnlyOneActiveReservationForSameSeat() throws Exception {
        var user = userRepository
                .save(User.builder().email("example@test.com").passwordHash("hashedpassword").role(Role.USER).build());
        var event = eventRepository
                .save(Event.builder().name("name").place("place").startsAt(Instant.now().plusSeconds(3600)).build());
        var seat = seatRepository
                .save(Seat.builder().event(event).seatRow(1).seatColumn("A").price(BigDecimal.valueOf(100.00)).build());

        int threadCount =  20;

        AtomicInteger successes = new AtomicInteger();
        AtomicInteger conflicts = new AtomicInteger();

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishedLatch = new CountDownLatch(threadCount);

        var authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    reservationService.create(seat.getId(), authentication);
                    successes.incrementAndGet();
                } catch (ReservationConflictException e) {
                    conflicts.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    finishedLatch.countDown();
                }
            }
        };

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount ; i++) {
            executor.submit(runnable);
        }

        readyLatch.await();

        startLatch.countDown();

        finishedLatch.await();

        assertEquals(1, successes.get());
        assertEquals(19, conflicts.get());
        assertEquals(1, reservationRepository.count());

        executor.shutdown();
    }
}
