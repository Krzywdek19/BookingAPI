package pl.exceptionhandled.bookingapi.reservation;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long id) {
        super("Reservation with id " + id + " was not found");
    }
}
