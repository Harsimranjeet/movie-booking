package com.movie.booking.theatre.service;

import com.movie.booking.theatre.dto.TheatreDtos.*;
import com.movie.booking.theatre.exception.ResourceNotFoundException;
import com.movie.booking.theatre.model.Screen;
import com.movie.booking.theatre.model.Theatre;
import com.movie.booking.theatre.repository.ScreenRepository;
import com.movie.booking.theatre.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepo;
    private final ScreenRepository screenRepo;

    public List<TheatreResponse> getAll() {
        log.debug("Fetching all active theatres");
        List<TheatreResponse> theatres = theatreRepo.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toDto)
                .toList();
        log.debug("Found {} active theatres", theatres.size());
        return theatres;
    }

    public TheatreResponse getById(String id) {
        log.debug("Fetching theatre by id={}", id);
        return toDto(findTheatre(UUID.fromString(id)));
    }

    public List<TheatreResponse> getByCity(String city) {
        log.debug("Fetching theatres in city='{}'", city);
        List<TheatreResponse> theatres = theatreRepo.findByCityIgnoreCaseAndActiveTrue(city)
                .stream()
                .map(this::toDto)
                .toList();
        log.debug("Found {} theatres in city='{}'", theatres.size(), city);
        return theatres;
    }

    @Transactional
    public TheatreResponse create(CreateTheatreRequest req) {
        log.info("Creating theatre: name='{}', city='{}', state='{}'", req.getName(), req.getCity(), req.getState());
        Theatre theatre = Theatre.builder()
                .name(req.getName())
                .address(req.getAddress())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .phone(req.getPhone())
                .email(req.getEmail())
                .build();

        TheatreResponse saved = toDto(theatreRepo.save(theatre));
        log.info("Theatre created: id={}, name='{}'", saved.id(), saved.name());
        return saved;
    }

    @Transactional
    public TheatreResponse update(String id, UpdateTheatreRequest req) {
        log.info("Updating theatre: id={}", id);
        Theatre theatre = findTheatre(UUID.fromString(id));

        if (req.getName() != null) theatre.setName(req.getName());
        if (req.getAddress() != null) theatre.setAddress(req.getAddress());
        if (req.getCity() != null) theatre.setCity(req.getCity());
        if (req.getState() != null) theatre.setState(req.getState());
        if (req.getPincode() != null) theatre.setPincode(req.getPincode());
        if (req.getPhone() != null) theatre.setPhone(req.getPhone());
        if (req.getEmail() != null) theatre.setEmail(req.getEmail());

        TheatreResponse updated = toDto(theatreRepo.save(theatre));
        log.info("Theatre updated: id={}, name='{}'", updated.id(), updated.name());
        return updated;
    }

    @Transactional
    public void delete(String id) {
        log.info("Soft-deleting theatre: id={}", id);
        Theatre theatre = findTheatre(UUID.fromString(id));
        theatre.setActive(false);
        theatreRepo.save(theatre);
        log.info("Theatre deactivated: id={}, name='{}'", id, theatre.getName());
    }

    public List<ScreenResponse> getScreens(String theatreId) {
        log.debug("Fetching screens for theatreId={}", theatreId);
        findTheatre(UUID.fromString(theatreId));

        List<ScreenResponse> screens = screenRepo.findByTheatreIdAndActiveTrue(UUID.fromString(theatreId))
                .stream()
                .map(this::toScreenDto)
                .toList();
        log.debug("Found {} active screens for theatreId={}", screens.size(), theatreId);
        return screens;
    }

    @Transactional
    public ScreenResponse addScreen(String theatreId, CreateScreenRequest req) {
        log.info("Adding screen to theatreId={}: name='{}', totalSeats={}, type='{}'",
            theatreId, req.getName(), req.getTotalSeats(), req.getType());
        findTheatre(UUID.fromString(theatreId));

        Screen.ScreenType type = Screen.ScreenType.REGULAR;

        if (req.getType() != null) {
            try {
                type = Screen.ScreenType.valueOf(req.getType());
            } catch (Exception ignored) {
            }
        }

        Screen screen = Screen.builder()
                .theatreId(UUID.fromString(theatreId))
                .name(req.getName())
                .totalSeats(req.getTotalSeats())
                .type(type)
                .build();

        ScreenResponse saved = toScreenDto(screenRepo.save(screen));
        log.info("Screen added: id={}, theatreId={}, name='{}', type={}", saved.id(), theatreId, saved.name(), saved.type());
        return saved;
    }

    @Transactional
    public void deleteScreen(String screenId) {
        log.info("Soft-deleting screen: id={}", screenId);
        Screen screen = screenRepo.findById(UUID.fromString(screenId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Screen not found: " + screenId)
                );

        screen.setActive(false);
        screenRepo.save(screen);
        log.info("Screen deactivated: id={}, name='{}'", screenId, screen.getName());
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