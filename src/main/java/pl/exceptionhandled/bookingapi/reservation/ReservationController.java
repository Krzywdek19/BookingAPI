package pl.exceptionhandled.bookingapi.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.exceptionhandled.bookingapi.reservation.dto.ReservationResponse;
import pl.exceptionhandled.bookingapi.security.AppUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getForUser(id, userDetails.getId(), userDetails.getRole()));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long seatId
    ) {
        if (userId != null) {
            return ResponseEntity.ok(reservationService.getAllByUser(userId));
        }
        if (seatId != null) {
            return ResponseEntity.ok(reservationService.getAllBySeat(seatId));
        }
        return ResponseEntity.ok(reservationService.getAll());
    }
}
