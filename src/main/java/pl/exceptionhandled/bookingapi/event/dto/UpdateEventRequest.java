package pl.exceptionhandled.bookingapi.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateEventRequest(
        @NotBlank String name,
        @NotBlank String place,
        @NotNull Instant startsAt
) {
}
