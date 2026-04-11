package com.movie.booking.booking.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity @Table(name="bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private UUID userId;

    @Column(nullable=false)
    private UUID showId;

    @Column(nullable=false)
    private UUID theatreId;

    @Column(nullable=false)
    private UUID movieId;

    @ElementCollection
    @CollectionTable(name="booking_seats", joinColumns=@JoinColumn(name="booking_id"))
    @Column(name="seat_id")
    private List<UUID> seatIds;

    @Column(nullable=false)
    private int ticketCount;

    @Column(nullable=false)
    private double totalAmount;

    private double discountAmount;

    private double finalAmount;

    private String offerCode;

    @Enumerated(EnumType.STRING) @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(unique=true) private String bookingRef;

    @CreationTimestamp private Instant createdAt;

    private Instant confirmedAt;

    private Instant cancelledAt;

    public enum BookingStatus { PENDING, CONFIRMED, CANCELLED, EXPIRED }
}
