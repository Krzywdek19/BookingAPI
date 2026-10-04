package pl.exceptionhandled.bookingapi.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserEmail(String email);

    List<Reservation> findAllByUserEmailAndSeatId(String email, Long seatId);

    List<Reservation> findAllBySeatIdOrderByCreatedAtDesc(Long seatId);

    List<Reservation> findAllByUserEmailOrderByCreatedAtDesc(String email);
}
