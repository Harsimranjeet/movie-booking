
package com.movie.booking.movie.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie implements Persistable<UUID> {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String language;

    @Column(nullable=false)
    private String genre;

    private int durationMins;

    private double rating;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;
}
