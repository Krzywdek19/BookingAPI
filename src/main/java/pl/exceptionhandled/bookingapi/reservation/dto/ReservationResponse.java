package pl.exceptionhandled.bookingapi.reservation.dto;

import pl.exceptionhandled.bookingapi.reservation.ReservationStatus;

import java.time.Instant;

public record ReservationResponse(
        Long id,
        Long userId,
        Long seatId,
        ReservationStatus status,
        Instant createdAt
) {
}
