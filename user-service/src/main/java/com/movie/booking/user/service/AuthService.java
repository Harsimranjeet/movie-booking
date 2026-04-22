package com.movie.booking.user.service;

import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.exception.BadRequestException;
import com.movie.booking.user.exception.ResourceNotFoundException;
import com.movie.booking.user.model.User;
import com.movie.booking.user.repository.UserRepository;
import com.movie.booking.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public AuthResponse signUp(SignUpRequest req) {
        log.info("Sign-up attempt: email='{}', role='{}'", req.getEmail(), req.getRole());
        if (userRepo.existsByEmail(req.getEmail())) {
            log.warn("Sign-up failed — email already registered: '{}'", req.getEmail());
            throw new BadRequestException("Email already registered");
        }
        if (req.getPhone() != null && userRepo.existsByPhone(req.getPhone())) {
            log.warn("Sign-up failed — phone already registered: '{}'", req.getPhone());
            throw new BadRequestException("Phone already registered");
        }

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
        log.info("User registered: id={}, email='{}', role={}", user.getId(), user.getEmail(), user.getRole());
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        log.info("Login attempt: identifier='{}'", req.getIdentifier());
        User user = userRepo.findByEmailOrPhone(req.getIdentifier(), req.getIdentifier())
            .orElseThrow(() -> {
                log.warn("Login failed — user not found: identifier='{}'", req.getIdentifier());
                return new BadRequestException("Invalid credentials");
            });

        if (!user.isActive()) {
            log.warn("Login failed — account disabled: userId={}, email='{}'", user.getId(), user.getEmail());
            throw new BadRequestException("Account is disabled");
        }

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed — wrong password: userId={}, email='{}'", user.getId(), user.getEmail());
            throw new BadRequestException("Invalid credentials");
        }

        log.info("Login successful: userId={}, email='{}', role={}", user.getId(), user.getEmail(), user.getRole());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, "Bearer", 86400, UserResponse.from(user));
    }
}
