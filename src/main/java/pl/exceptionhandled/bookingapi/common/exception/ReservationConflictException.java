package pl.exceptionhandled.bookingapi.common.exception;

public class ReservationConflictException extends RuntimeException {
    public ReservationConflictException() {
        super("The seat is already reserved");
    }
}
