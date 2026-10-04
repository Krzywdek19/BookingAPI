package pl.exceptionhandled.bookingapi.seat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.exceptionhandled.bookingapi.event.Event;
import pl.exceptionhandled.bookingapi.event.EventRepository;
import pl.exceptionhandled.bookingapi.seat.dto.SeatRequest;
import pl.exceptionhandled.bookingapi.seat.dto.SeatResponse;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {
    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SeatService service;

    @Test
    void shouldCreateSeatForExistingEvent() {
        Event event = Event.builder().id(1L).name("Concert").place("Hall").build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SeatResponse result = service.create(new SeatRequest(1L, "A", 2, BigDecimal.TEN));

        assertEquals(1L, result.eventId());
        assertEquals("A", result.seatColumn());
        assertEquals(2, result.seatRow());
        assertEquals(BigDecimal.TEN, result.price());
        verify(seatRepository).save(any(Seat.class));
    }

    @Test
    void shouldThrowWhenSeatDoesNotExist() {
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SeatNotFoundException.class, () -> service.get(99L));
    }
}
