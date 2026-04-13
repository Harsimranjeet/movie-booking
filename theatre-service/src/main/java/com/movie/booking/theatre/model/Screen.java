package com.movie.booking.theatre.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen implements Persistable<UUID> {

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

    @Column(name = "theatre_id", nullable = false)
    private UUID theatreId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int totalSeats;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ScreenType type = ScreenType.REGULAR;

    @Builder.Default
    private boolean active = true;

    public enum ScreenType {
        REGULAR,
        IMAX,
        DOLBY,
        FOUR_DX
    }
}