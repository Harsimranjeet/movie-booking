package com.movie.booking.show.dto;

import com.movie.booking.show.model.Show;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ShowDtos {

    @Data
    public static class CreateShowRequest {

        @NotNull
        private UUID movieId;

        @NotNull
        private UUID theatreId;

        @NotNull
        private UUID screenId;

        @NotNull
        private LocalDate showDate;

        @NotNull
        private LocalTime startTime;

        @NotNull
        private LocalTime endTime;

        @NotBlank
        private String language;

        private String format;

        @Min(1)
        private double basePrice;
    }

    @Data
    public static class UpdateShowRequest {

        private LocalDate showDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String language;
        private String format;
        private Double basePrice;
        private String status;
    }

    public record ShowResponse(
            UUID id,
            UUID movieId,
            UUID theatreId,
            UUID screenId,
            LocalDate showDate,
            LocalTime startTime,
            LocalTime endTime,
            String language,
            String format,
            double basePrice,
            Show.ShowStatus status
    ) {}
}