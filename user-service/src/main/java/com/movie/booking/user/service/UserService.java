package com.movie.booking.user.service;

import com.movie.booking.user.dto.UserDtos.*;
import com.movie.booking.user.exception.BadRequestException;
import com.movie.booking.user.exception.ResourceNotFoundException;
import com.movie.booking.user.model.User;
import com.movie.booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;

    public UserResponse getById(UUID id) {
        log.debug("Fetching user by id={}", id);
        return UserResponse.from(findOrThrow(id));
    }

    public List<UserResponse> getAll() {
        log.debug("Fetching all users");
        List<UserResponse> users = userRepo.findAll().stream().map(UserResponse::from).toList();
        log.debug("Found {} users", users.size());
        return users;
    }

    @Transactional
    public UserResponse updateProfile(UUID id, UpdateProfileRequest req) {
        log.info("Updating profile for userId={}", id);
        User user = findOrThrow(id);
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) {
            if (userRepo.existsByPhone(req.getPhone()) && !req.getPhone().equals(user.getPhone())) {
                log.warn("Profile update failed — phone already in use: userId={}, phone='{}'", id, req.getPhone());
                throw new BadRequestException("Phone already in use");
            }
            user.setPhone(req.getPhone());
        }
        UserResponse updated = UserResponse.from(userRepo.save(user));
        log.info("Profile updated: userId={}", id);
        return updated;
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest req) {
        log.info("Password change requested for userId={}", id);
        User user = findOrThrow(id);
        if (!encoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Password change failed — incorrect current password: userId={}", id);
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(encoder.encode(req.getNewPassword()));
        userRepo.save(user);
        log.info("Password changed successfully: userId={}", id);
    }

    @Transactional
    public void deactivate(UUID id) {
        log.info("Deactivating user: userId={}", id);
        User user = findOrThrow(id);
        user.setActive(false);
        userRepo.save(user);
        log.info("User deactivated: userId={}, email='{}'", id, user.getEmail());
    }

    private User findOrThrow(UUID id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
