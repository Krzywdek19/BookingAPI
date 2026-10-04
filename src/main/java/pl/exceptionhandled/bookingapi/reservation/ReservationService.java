package pl.exceptionhandled.bookingapi.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.exceptionhandled.bookingapi.common.exception.ReservationAccessDeniedException;
import pl.exceptionhandled.bookingapi.reservation.dto.ReservationResponse;
import pl.exceptionhandled.bookingapi.user.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationResponse get(Long id) {
        return toResponse(reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id)));
    }

    public ReservationResponse getForUser(
            Long reservationId,
            Long currentUserId,
            Role role
    ) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        boolean isOwner =
                reservation.getUser().getId().equals(currentUserId);

        boolean isAdmin =
                role == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ReservationAccessDeniedException();
        }

        return toResponse(reservation);
    }

    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReservationResponse> getAllByUser(Long userId) {
        return reservationRepository.findAllByUserId(userId).stream().map(this::toResponse).toList();
    }

    public List<ReservationResponse> getAllBySeat(Long seatId) {
        return reservationRepository.findAllBySeatId(seatId).stream().map(this::toResponse).toList();
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getSeat().getId(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
