package com.movie.booking.payment.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false, unique=true)
    private UUID bookingId;

    @Column(nullable=false)
    private UUID userId;

    @Column(nullable=false)
    private double amount;

    @Column(nullable=false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)

    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.INITIATED;

    private String transactionId;

    private String gatewayResponse;

    private String failureReason;

    @CreationTimestamp private Instant createdAt;
    private Instant processedAt;

    public enum PaymentMethod { CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET }
    public enum PaymentStatus { INITIATED, PENDING, SUCCESS, FAILED, REFUNDED }
}
