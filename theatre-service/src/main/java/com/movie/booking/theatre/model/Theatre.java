
package com.movie.booking.theatre.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name="theatres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Theatre {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String address;
    @Column(nullable=false) private String city;
    private String state;
    private String pincode;
    private String phone;
    private String email;
    @Builder.Default private boolean active = true;
}
