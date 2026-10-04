package pl.exceptionhandled.bookingapi.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.exceptionhandled.bookingapi.event.dto.EventResponse;
import pl.exceptionhandled.bookingapi.event.dto.UpdateEventRequest;
import pl.exceptionhandled.bookingapi.event.dto.CreateEventRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse create(CreateEventRequest request) {
        Event event = Event.builder()
                .name(request.name())
                .place(request.place())
                .startsAt(request.startsAt())
                .build();

        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponse get(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public EventResponse update(Long id, UpdateEventRequest request) {
        Event event = findById(id);
        event.update(request.name(), request.place(), request.startsAt());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getPlace(),
                event.getStartsAt(),
                event.getCreatedAt()
        );
    }
}