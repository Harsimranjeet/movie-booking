package com.movie.booking.user.controller;

import com.movie.booking.user.dto.ApiResponse;
import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User registration and login")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user",
               description = "Creates a new user account and returns a JWT access token.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
            description = "Registration successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Validation error or email already taken")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signUp(@Valid @RequestBody SignUpRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Registration successful", authService.signUp(req)));
    }

    @Operation(summary = "Login",
               description = "Authenticates a user by email/phone and password, returns a JWT.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
            description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(req)));
    }
}
