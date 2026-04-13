package com.movie.booking.show.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show implements Persistable<UUID> {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @PostPersist @PostLoad
    void markNotNew() { this.isNew = false; }

    @Override public UUID getId() { return id; }
    @Override public boolean isNew() { return isNew; }

    @Column(nullable = false)
    private UUID movieId;

    @Column(nullable = false)
    private UUID theatreId;

    @Column(nullable = false)
    private UUID screenId;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String language;

    private String format;

    @Column(nullable = false)
    private double basePrice;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShowStatus status = ShowStatus.SCHEDULED;

    public enum ShowStatus {
        SCHEDULED,
        OPEN,
        HOUSEFULL,
        CANCELLED,
        COMPLETED
    }
}