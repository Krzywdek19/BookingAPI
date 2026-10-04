package pl.exceptionhandled.bookingapi.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateEventRequest(
        @NotBlank @jakarta.validation.constraints.Size(max = 255) String name,
        @NotBlank @jakarta.validation.constraints.Size(max = 255) String place,
        @NotNull Instant startsAt
) {
}
