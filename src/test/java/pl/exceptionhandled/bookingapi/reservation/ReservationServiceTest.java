package pl.exceptionhandled.bookingapi.reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.exceptionhandled.bookingapi.common.exception.ReservationAccessDeniedException;
import pl.exceptionhandled.bookingapi.reservation.dto.ReservationResponse;
import pl.exceptionhandled.bookingapi.seat.Seat;
import pl.exceptionhandled.bookingapi.seat.SeatRepository;
import pl.exceptionhandled.bookingapi.user.Role;
import pl.exceptionhandled.bookingapi.user.User;
import pl.exceptionhandled.bookingapi.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService service;

    @Test
    void shouldCreatePendingReservationForAuthenticatedUser() {
        User user = user("user@example.com", Role.USER);
        Seat seat = Seat.builder().id(7L).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(seatRepository.findById(7L)).thenReturn(Optional.of(seat));
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation = invocation.getArgument(0);
                    return Reservation.builder()
                            .id(1L)
                            .user(reservation.getUser())
                            .seat(reservation.getSeat())
                            .status(reservation.getStatus())
                            .build();
                });

        ReservationResponse response = service.create(7L, authentication("user@example.com", Role.USER));

        assertEquals(ReservationStatus.PENDING, response.status());
        assertEquals(7L, response.seatId());
        verify(reservationRepository).saveAndFlush(any(Reservation.class));
    }

    @Test
    void shouldRejectReservationReadForDifferentUser() {
        User owner = user("owner@example.com", Role.USER);
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(owner)
                .seat(Seat.builder().id(7L).build())
                .status(ReservationStatus.PENDING)
                .build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationAccessDeniedException.class,
                () -> service.get(1L, authentication("other@example.com", Role.USER))
        );
    }

    @Test
    void shouldAllowAdminToCancelAnyReservation() {
        User owner = user("owner@example.com", Role.USER);
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(owner)
                .seat(Seat.builder().id(7L).build())
                .status(ReservationStatus.PENDING)
                .build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = service.cancel(
                1L,
                authentication("admin@example.com", Role.ADMIN)
        );

        assertEquals(ReservationStatus.CANCELLED, response.status());
    }

    private User user(String email, Role role) {
        return User.builder().id(email.startsWith("owner") ? 2L : 1L)
                .email(email)
                .role(role)
                .build();
    }

    private UsernamePasswordAuthenticationToken authentication(String email, Role role) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
}
