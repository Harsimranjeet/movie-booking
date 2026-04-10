package com.movie.booking.offer.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name="offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Offer {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(nullable=false, unique=true)
    private String code;
    @Column(nullable=false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private DiscountType discountType;
    @Column(nullable=false)
    private double discountValue;
    private double maxDiscount;
    private int    minTickets;
    @Column(nullable=false)
    private LocalDate validFrom;
    @Column(nullable=false)
    private LocalDate validTo;
    private int maxUsesTotal;
    private int usedCount;
    private int maxUsesPerUser;
    @Builder.Default
    private boolean active = true;

    public enum DiscountType { PERCENTAGE, FLAT, NTH_TICKET, MATINEE }
}
