package com.movie.booking.show.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity @Table(name="shows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Show {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(nullable=false) private UUID movieId;
    @Column(nullable=false) private UUID theatreId;
    @Column(nullable=false) private UUID screenId;
    @Column(nullable=false) private LocalDate showDate;
    @Column(nullable=false) private LocalTime startTime;
    @Column(nullable=false) private LocalTime endTime;
    @Column(nullable=false) private String language;
    private String format;
    @Column(nullable=false) private double basePrice;
    @Enumerated(EnumType.STRING) @Builder.Default private ShowStatus status = ShowStatus.SCHEDULED;
    public enum ShowStatus { SCHEDULED, OPEN, HOUSEFULL, CANCELLED, COMPLETED }
}
