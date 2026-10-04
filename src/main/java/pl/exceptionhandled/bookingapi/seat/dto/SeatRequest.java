package pl.exceptionhandled.bookingapi.seat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SeatRequest(
        @NotNull Long eventId,
        @NotBlank @jakarta.validation.constraints.Size(max = 2) String seatColumn,
        @NotNull @Positive Integer seatRow,
        @NotNull @PositiveOrZero BigDecimal price
) {
}
