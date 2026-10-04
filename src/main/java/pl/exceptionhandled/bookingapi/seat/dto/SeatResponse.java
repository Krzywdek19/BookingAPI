package pl.exceptionhandled.bookingapi.seat.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SeatResponse(
        Long id,
        Long eventId,
        String seatColumn,
        Integer seatRow,
        BigDecimal price,
        Instant createdAt
) {
}
