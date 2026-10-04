package pl.exceptionhandled.bookingapi.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserId(Long userId);

    List<Reservation> findAllBySeatId(Long seatId);
}
