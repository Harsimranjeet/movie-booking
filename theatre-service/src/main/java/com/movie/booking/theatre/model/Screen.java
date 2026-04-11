package com.movie.booking.theatre.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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