
package com.movie.booking.theatre.dto;
import com.movie.booking.theatre.model.Screen;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

public class TheatreDtos {

    @Data public static class CreateTheatreRequest {
        @NotBlank private String name;
        @NotBlank private String address;
        @NotBlank private String city;
        private String state, pincode, phone, email;
    }

    @Data public static class UpdateTheatreRequest {
        private String name, address, city, state, pincode, phone, email;
    }

    @Data public static class CreateScreenRequest {
        @NotBlank private String name;
        @Min(1)   private int totalSeats;
        private String type;
    }

    public record TheatreResponse(UUID id, String name, String address, String city,
        String state, String pincode, String phone, String email, boolean active) {}

    public record ScreenResponse(UUID id, UUID theatreId, String name,
        int totalSeats, Screen.ScreenType type, boolean active) {}
}
