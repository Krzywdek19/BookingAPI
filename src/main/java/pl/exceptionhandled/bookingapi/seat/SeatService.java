package pl.exceptionhandled.bookingapi.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.bookingapi.event.Event;
import pl.exceptionhandled.bookingapi.event.EventNotFoundException;
import pl.exceptionhandled.bookingapi.event.EventRepository;
import pl.exceptionhandled.bookingapi.seat.dto.SeatRequest;
import pl.exceptionhandled.bookingapi.seat.dto.SeatResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatResponse create(SeatRequest request) {
        Seat seat = Seat.builder()
                .event(findEvent(request.eventId()))
                .seatColumn(request.seatColumn())
                .seatRow(request.seatRow())
                .price(request.price())
                .build();
        return toResponse(seatRepository.save(seat));
    }

    public SeatResponse get(Long id) {
        return toResponse(findById(id));
    }

    public List<SeatResponse> getAll() {
        return seatRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<SeatResponse> getAllByEvent(Long eventId) {
        findEvent(eventId);
        return seatRepository.findAllByEventId(eventId).stream().map(this::toResponse).toList();
    }

    public SeatResponse update(Long id, SeatRequest request) {
        Seat seat = findById(id);
        seat.update(findEvent(request.eventId()), request.seatColumn(), request.seatRow(), request.price());
        return toResponse(seatRepository.save(seat));
    }

    public void delete(Long id) {
        seatRepository.delete(findById(id));
    }

    private Seat findById(Long id) {
        return seatRepository.findById(id).orElseThrow(() -> new SeatNotFoundException(id));
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    private SeatResponse toResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getEvent().getId(),
                seat.getSeatColumn(),
                seat.getSeatRow(),
                seat.getPrice(),
                seat.getCreatedAt()
        );
    }
}
