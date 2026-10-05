package pl.exceptionhandled.bookingapi.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Request is invalid");
    }

    @ExceptionHandler(EmailIsAlreadyTakenException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(EmailIsAlreadyTakenException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation() {
        return error(HttpStatus.CONFLICT, "The requested operation conflicts with existing data");
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ApiError> handleReservationConflict(ReservationConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationFailure() {
        return error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied() {
        return error(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(ReservationAccessDeniedException.class)
    public ResponseEntity<ApiError> handleReservationAccessDenied(
            ReservationAccessDeniedException exception
    ) {
        return error(HttpStatus.FORBIDDEN, "Access denied");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(message));
    }
}
