package pl.exceptionhandled.bookingapi.seat;

import jakarta.persistence.*;
import lombok.*;
import pl.exceptionhandled.bookingapi.event.Event;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "seat_column", nullable = false, length = 2)
    private String seatColumn;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    void update(Event event, String seatColumn, Integer seatRow, BigDecimal price) {
        this.event = event;
        this.seatColumn = seatColumn;
        this.seatRow = seatRow;
        this.price = price;
    }
}