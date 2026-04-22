package com.movie.booking.seat.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="seats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seat {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private UUID showId;

    @Column(nullable=false)
    private UUID screenId;

    @Column(nullable=false)
    private String seatNumber;

    @Column(nullable=false)
    private String rowLabel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatCategory category = SeatCategory.REGULAR;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    private UUID lockedByBookingId;

    private Instant lockExpiresAt;

    @Column(nullable=false) private double price;

    @Version private Long version;

    public enum SeatCategory {
        REGULAR, PREMIUM, VIP, RECLINER
    }

    public enum SeatStatus   {
        AVAILABLE, LOCKED, BOOKED, MAINTENANCE
    }
}
