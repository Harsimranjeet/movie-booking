package com.movie.booking.user.service;

import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.exception.BadRequestException;
import com.movie.booking.user.exception.ResourceNotFoundException;
import com.movie.booking.user.model.User;
import com.movie.booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;

    public UserResponse getById(UUID id) {
        return UserResponse.from(findOrThrow(id));
    }

    public List<UserResponse> getAll() {
        return userRepo.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse updateProfile(UUID id, UpdateProfileRequest req) {
        User user = findOrThrow(id);
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) {
            if (userRepo.existsByPhone(req.getPhone()) && !req.getPhone().equals(user.getPhone()))
                throw new BadRequestException("Phone already in use");
            user.setPhone(req.getPhone());
        }
        return UserResponse.from(userRepo.save(user));
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest req) {
        User user = findOrThrow(id);
        if (!encoder.matches(req.getCurrentPassword(), user.getPasswordHash()))
            throw new BadRequestException("Current password is incorrect");
        user.setPasswordHash(encoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = findOrThrow(id);
        user.setActive(false);
        userRepo.save(user);
    }

    private User findOrThrow(UUID id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
