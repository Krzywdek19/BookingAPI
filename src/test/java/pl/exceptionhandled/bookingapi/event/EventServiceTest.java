package pl.exceptionhandled.bookingapi.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.exceptionhandled.bookingapi.event.dto.CreateEventRequest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static java.util.Optional.empty;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;

    private static final String NAME = "Magic Knight Recruitment";
    private static final String PLACE = "Clover";
    private static final Instant STARTS_AT =
            Instant.parse("2026-10-10T12:00:00Z");

    @Test
    void shouldCreateAndReturnEventWhenRequestIsValid() {
        var request = new CreateEventRequest(
                NAME,
                PLACE,
                STARTS_AT
        );

        when(repository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var captor = ArgumentCaptor.forClass(Event.class);

        var result = service.create(request);

        verify(repository).save(captor.capture());

        var savedEvent = captor.getValue();

        assertEquals(NAME, savedEvent.getName());
        assertEquals(PLACE, savedEvent.getPlace());
        assertEquals(STARTS_AT, savedEvent.getStartsAt());

        assertEquals(NAME, result.name());
        assertEquals(PLACE, result.place());
        assertEquals(STARTS_AT, result.startsAt());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {
        when(repository.findById(99L)).thenReturn(empty());

        assertThrows(EventNotFoundException.class, () -> service.get(99L));
    }
}