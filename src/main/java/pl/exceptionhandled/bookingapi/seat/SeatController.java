package pl.exceptionhandled.bookingapi.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.exceptionhandled.bookingapi.seat.dto.SeatRequest;
import pl.exceptionhandled.bookingapi.seat.dto.SeatResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponse> create(@Valid @RequestBody SeatRequest request) {
        SeatResponse response = seatService.create(request);
        return ResponseEntity.created(URI.create("/api/seats/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAll(
            @RequestParam(required = false) Long eventId
    ) {
        return ResponseEntity.ok(eventId == null
                ? seatService.getAll()
                : seatService.getAllByEvent(eventId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SeatRequest request
    ) {
        return ResponseEntity.ok(seatService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
