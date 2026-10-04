package pl.exceptionhandled.bookingapi.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.exceptionhandled.bookingapi.common.exception.ReservationAccessDeniedException;
import pl.exceptionhandled.bookingapi.common.exception.ReservationConflictException;
import pl.exceptionhandled.bookingapi.reservation.dto.ReservationResponse;
import pl.exceptionhandled.bookingapi.seat.Seat;
import pl.exceptionhandled.bookingapi.seat.SeatNotFoundException;
import pl.exceptionhandled.bookingapi.seat.SeatRepository;
import pl.exceptionhandled.bookingapi.user.Role;
import pl.exceptionhandled.bookingapi.user.User;
import pl.exceptionhandled.bookingapi.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponse create(
            Long seatId,
            Authentication authentication
    ) {
        User user = currentUser(authentication);
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        Reservation reservation = Reservation.builder()
                .user(user)
                .seat(seat)
                .status(ReservationStatus.PENDING)
                .build();

        try {
            return toResponse(reservationRepository.saveAndFlush(reservation));
        } catch (DataIntegrityViolationException exception) {
            throw new ReservationConflictException();
        }
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(Long reservationId, Authentication authentication) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        checkAccess(reservation, authentication);

        return toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAll(Authentication authentication, Long seatId) {
        List<Reservation> reservations;
        if (isAdmin(authentication)) {
            reservations = seatId == null
                    ? reservationRepository.findAll()
                    : reservationRepository.findAllBySeatIdOrderByCreatedAtDesc(seatId);
        } else {
            reservations = seatId == null
                    ? reservationRepository.findAllByUserEmailOrderByCreatedAtDesc(authentication.getName())
                    : reservationRepository.findAllByUserEmailAndSeatId(authentication.getName(), seatId);
        }
        return reservations.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReservationResponse cancel(Long reservationId, Authentication authentication) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        checkAccess(reservation, authentication);
        reservation.cancel();
        return toResponse(reservation);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        authentication.getName()
                ));
    }

    private void checkAccess(Reservation reservation, Authentication authentication) {
        boolean isOwner = reservation.getUser().getEmail().equals(authentication.getName());
        if (!isOwner && !isAdmin(authentication)) {
            throw new ReservationAccessDeniedException();
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
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
