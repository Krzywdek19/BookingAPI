package pl.exceptionhandled.bookingapi.reservation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.exceptionhandled.bookingapi.reservation.dto.CreateReservationRequest;
import pl.exceptionhandled.bookingapi.reservation.dto.ReservationResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest request,
            Authentication authentication
    ) {
        ReservationResponse response = reservationService.create(request.seatId(), authentication);
        return ResponseEntity.created(URI.create("/api/reservations/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(reservationService.get(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll(
            @RequestParam(required = false) Long seatId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reservationService.getAll(authentication, seatId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponse> cancel(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(reservationService.cancel(id, authentication));
    }
}
