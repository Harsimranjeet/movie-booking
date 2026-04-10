package com.movie.booking.user.service;

import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.exception.BadRequestException;
import com.movie.booking.user.exception.ResourceNotFoundException;
import com.movie.booking.user.model.User;
import com.movie.booking.user.repository.UserRepository;
import com.movie.booking.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil        jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public AuthResponse signUp(SignUpRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already registered");
        if (req.getPhone() != null && userRepo.existsByPhone(req.getPhone()))
            throw new BadRequestException("Phone already registered");

        User.Role role = User.Role.CUSTOMER;
        if (req.getRole() != null) {
            try { role = User.Role.valueOf(req.getRole().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        User user = User.builder()
            .fullName(req.getFullName())
            .email(req.getEmail())
            .passwordHash(encoder.encode(req.getPassword()))
            .phone(req.getPhone())
            .role(role)
            .build();

        userRepo.save(user);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmailOrPhone(req.getIdentifier(), req.getIdentifier())
            .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!user.isActive())
            throw new BadRequestException("Account is disabled");

        if (!encoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new BadRequestException("Invalid credentials");

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, "Bearer", 86400, UserResponse.from(user));
    }
}
