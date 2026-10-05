package pl.exceptionhandled.bookingapi.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUserEmail(String email);

    List<Reservation> findAllByUserEmailAndSeatId(String email, Long seatId);

    List<Reservation> findAllBySeatIdOrderByCreatedAtDesc(Long seatId);

    List<Reservation> findAllByUserEmailOrderByCreatedAtDesc(String email);

    boolean existsBySeatIdAndStatusIn(
            Long seatId,
            List<ReservationStatus> statuses
    );
}
