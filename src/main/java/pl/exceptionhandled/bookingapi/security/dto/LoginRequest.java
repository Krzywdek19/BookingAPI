package pl.exceptionhandled.bookingapi.security.dto;

public record LoginRequest(
        String email,
        String password
) {}