package pl.exceptionhandled.bookingapi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.exceptionhandled.bookingapi.event.EventNotFoundException;
import pl.exceptionhandled.bookingapi.reservation.ReservationNotFoundException;
import pl.exceptionhandled.bookingapi.seat.SeatNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            EventNotFoundException.class,
            SeatNotFoundException.class,
            ReservationNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(exception.getMessage()));
    }
}
