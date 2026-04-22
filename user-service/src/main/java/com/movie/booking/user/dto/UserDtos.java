package com.movie.booking.user.dto;

import com.movie.booking.user.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

public class UserDtos {

    @Data
    public static class SignUpRequest {
        @NotBlank
        @Size(min = 2, max = 100)
        private String fullName;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                 message = "Password must contain letters and numbers")
        private String password;

        @Pattern(regexp = "^[+]?[0-9]{10,13}$", message = "Invalid phone")
        private String phone;

        private String role;
    }

    @Data
    public static class LoginRequest {

        @NotBlank(message = "Email or phone required")
        private String identifier;

        @NotBlank
        private String password;
    }

    @Data
    public static class UpdateProfileRequest {
        @Size(min = 2, max = 100)
        private String fullName;

        @Pattern(regexp = "^[+]?[0-9]{10,13}$", message = "Invalid phone")
        private String phone;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank private String currentPassword;
        @NotBlank @Size(min = 8) private String newPassword;
    }

    public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user
    ) {}

    public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String role,
        boolean active,
        Instant createdAt
    ) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getFullName(), u.getEmail(),
                u.getPhone(), u.getRole().name(), u.isActive(), u.getCreatedAt());
        }
    }
}
