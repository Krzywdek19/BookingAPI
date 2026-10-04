package pl.exceptionhandled.bookingapi.seat;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(Long id) {
        super("Seat with id " + id + " was not found");
    }
}
