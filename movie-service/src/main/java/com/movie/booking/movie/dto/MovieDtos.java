
package com.movie.booking.movie.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

public class MovieDtos {

    @Data public static class CreateMovieRequest {
        @NotBlank
        private String title;

        @NotBlank
        private String language;

        @NotBlank
        private String genre;

        @Min(1)
        private int durationMins;
    }

    @Data public static class UpdateMovieRequest {
        private String title;
        private String language;
        private String genre;
        private int durationMins;
        private Double rating;
    }

    public record MovieResponse(
        UUID id, String title, String language,
        String genre, int durationMins, double rating,
        boolean active, Instant createdAt) {}
}
