
package com.movie.booking.movie.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name="languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(nullable=false, unique=true) private String code;
    @Column(nullable=false)              private String name;
}
