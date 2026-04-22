
package com.movie.booking.movie.model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;
import java.util.UUID;

@Entity
@Table(name="genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre implements Persistable<UUID> {

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

    @Column(nullable=false, unique=true)
    private String name;
}
