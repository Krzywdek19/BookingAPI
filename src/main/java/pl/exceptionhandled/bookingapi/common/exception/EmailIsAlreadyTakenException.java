package pl.exceptionhandled.bookingapi.common.exception;

public class EmailIsAlreadyTakenException extends RuntimeException {
    public EmailIsAlreadyTakenException(String email) {
        super("Email %s is already taken.".formatted(email));
    }
}
