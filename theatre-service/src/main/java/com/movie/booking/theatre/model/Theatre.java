package com.movie.booking.theatre.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "theatres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre implements Persistable<UUID> {

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
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    private String state;

    private String pincode;

    private String phone;

    private String email;

    @Builder.Default
    private boolean active = true;
}