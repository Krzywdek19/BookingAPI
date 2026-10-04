package pl.exceptionhandled.bookingapi.reservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReservationRequest(
        @NotNull @Positive Long seatId
) {
}
