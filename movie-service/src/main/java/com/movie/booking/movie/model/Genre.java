
package com.movie.booking.movie.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name="genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Genre {

    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Column(nullable=false, unique=true) private String name;
    private String description;
}
