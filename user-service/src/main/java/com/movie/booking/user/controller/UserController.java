package com.movie.booking.user.controller;

import com.movie.booking.user.dto.ApiResponse;
import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get own profile",
               description = "Returns the authenticated user's profile. User ID is injected by the API gateway.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved", userService.getById(userId)));
    }

    @Operation(summary = "Get user by ID (Admin)",
               description = "Returns any user by their UUID. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(params = "id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved", userService.getById(id)));
    }

    @Operation(summary = "List all users (Admin)",
               description = "Returns all registered users. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", userService.getAll()));
    }

    @Operation(summary = "Update own profile",
               description = "Updates the authenticated user's full name and/or phone number.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", userService.updateProfile(userId, req)));
    }

    @Operation(summary = "Change own password",
               description = "Changes the authenticated user's password after verifying the current one.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Wrong current password or validation error")
    })
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(userId, req);
        return ResponseEntity.ok(ApiResponse.ok("Password changed"));
    }

    @Operation(summary = "Deactivate user (Admin)",
               description = "Soft-deletes (deactivates) a user account. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deactivated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@RequestParam("id") String id) {
        userService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok("User deactivated"));
    }
}
