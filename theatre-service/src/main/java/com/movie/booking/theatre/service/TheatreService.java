package com.movie.booking.theatre.service;

import com.movie.booking.theatre.dto.TheatreDtos.*;
import com.movie.booking.theatre.exception.ResourceNotFoundException;
import com.movie.booking.theatre.model.Screen;
import com.movie.booking.theatre.model.Theatre;
import com.movie.booking.theatre.repository.ScreenRepository;
import com.movie.booking.theatre.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepo;
    private final ScreenRepository screenRepo;

    public List<TheatreResponse> getAll() {
        return theatreRepo.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public TheatreResponse getById(UUID id) {
        return toDto(findTheatre(id));
    }

    public List<TheatreResponse> getByCity(String city) {
        return theatreRepo.findByCityIgnoreCaseAndActiveTrue(city)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public TheatreResponse create(CreateTheatreRequest req) {
        Theatre theatre = Theatre.builder()
                .name(req.getName())
                .address(req.getAddress())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .phone(req.getPhone())
                .email(req.getEmail())
                .build();

        return toDto(theatreRepo.save(theatre));
    }

    @Transactional
    public TheatreResponse update(UUID id, UpdateTheatreRequest req) {
        Theatre theatre = findTheatre(id);

        if (req.getName() != null) theatre.setName(req.getName());
        if (req.getAddress() != null) theatre.setAddress(req.getAddress());
        if (req.getCity() != null) theatre.setCity(req.getCity());
        if (req.getState() != null) theatre.setState(req.getState());
        if (req.getPincode() != null) theatre.setPincode(req.getPincode());
        if (req.getPhone() != null) theatre.setPhone(req.getPhone());
        if (req.getEmail() != null) theatre.setEmail(req.getEmail());

        return toDto(theatreRepo.save(theatre));
    }

    @Transactional
    public void delete(UUID id) {
        Theatre theatre = findTheatre(id);
        theatre.setActive(false);
        theatreRepo.save(theatre);
    }

    public List<ScreenResponse> getScreens(UUID theatreId) {
        findTheatre(theatreId);

        return screenRepo.findByTheatreIdAndActiveTrue(theatreId)
                .stream()
                .map(this::toScreenDto)
                .toList();
    }

    @Transactional
    public ScreenResponse addScreen(UUID theatreId, CreateScreenRequest req) {
        findTheatre(theatreId);

        Screen.ScreenType type = Screen.ScreenType.REGULAR;

        if (req.getType() != null) {
            try {
                type = Screen.ScreenType.valueOf(req.getType());
            } catch (Exception ignored) {
            }
        }

        Screen screen = Screen.builder()
                .theatreId(theatreId)
                .name(req.getName())
                .totalSeats(req.getTotalSeats())
                .type(type)
                .build();

        return toScreenDto(screenRepo.save(screen));
    }

    @Transactional
    public void deleteScreen(UUID screenId) {
        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Screen not found: " + screenId)
                );

        screen.setActive(false);
        screenRepo.save(screen);
    }

    private Theatre findTheatre(UUID id) {
        return theatreRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Theatre not found: " + id)
                );
    }

    private TheatreResponse toDto(Theatre theatre) {
        return new TheatreResponse(
                theatre.getId(),
                theatre.getName(),
                theatre.getAddress(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPincode(),
                theatre.getPhone(),
                theatre.getEmail(),
                theatre.isActive()
        );
    }

    private ScreenResponse toScreenDto(Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getTheatreId(),
                screen.getName(),
                screen.getTotalSeats(),
                screen.getType(),
                screen.isActive()
        );
    }
}