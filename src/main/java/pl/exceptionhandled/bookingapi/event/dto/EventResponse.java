package pl.exceptionhandled.bookingapi.event.dto;

import java.time.Instant;

public record EventResponse(
        Long id,
        String name,
        String place,
        Instant startsAt,
        Instant createdAt
) {
}
