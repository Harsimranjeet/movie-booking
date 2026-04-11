
package com.movie.booking.movie.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Movie {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private String title;

    @Column(length=2000)
    private String description;

    @Column(nullable=false)
    private String language;

    @Column(nullable=false)
    private String genre;

    private int durationMins;

    private String posterUrl;

    private String trailerUrl;

    private String certification;

    private double rating;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;
}
