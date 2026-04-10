
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

@Service @RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepo;
    private final ScreenRepository  screenRepo;

    public List<TheatreResponse> getAll()             { return theatreRepo.findByActiveTrueOrderByNameAsc().stream().map(this::toDto).toList(); }
    public TheatreResponse getById(UUID id)           { return toDto(findTheatre(id)); }
    public List<TheatreResponse> getByCity(String c)  { return theatreRepo.findByCityIgnoreCaseAndActiveTrue(c).stream().map(this::toDto).toList(); }

    @Transactional
    public TheatreResponse create(CreateTheatreRequest req) {
        Theatre t = Theatre.builder().name(req.getName()).address(req.getAddress())
            .city(req.getCity()).state(req.getState()).pincode(req.getPincode())
            .phone(req.getPhone()).email(req.getEmail()).build();
        return toDto(theatreRepo.save(t));
    }

    @Transactional
    public TheatreResponse update(UUID id, UpdateTheatreRequest req) {
        Theatre t = findTheatre(id);
        if (req.getName()    != null) t.setName(req.getName());
        if (req.getAddress() != null) t.setAddress(req.getAddress());
        if (req.getCity()    != null) t.setCity(req.getCity());
        if (req.getState()   != null) t.setState(req.getState());
        if (req.getPincode() != null) t.setPincode(req.getPincode());
        if (req.getPhone()   != null) t.setPhone(req.getPhone());
        if (req.getEmail()   != null) t.setEmail(req.getEmail());
        return toDto(theatreRepo.save(t));
    }

    @Transactional
    public void delete(UUID id) { Theatre t = findTheatre(id); t.setActive(false); theatreRepo.save(t); }

    public List<ScreenResponse> getScreens(UUID theatreId) {
        findTheatre(theatreId);
        return screenRepo.findByTheatreIdAndActiveTrue(theatreId).stream().map(this::toScreenDto).toList();
    }

    @Transactional
    public ScreenResponse addScreen(UUID theatreId, CreateScreenRequest req) {
        findTheatre(theatreId);
        Screen.ScreenType type = Screen.ScreenType.REGULAR;
        if (req.getType() != null) try { type = Screen.ScreenType.valueOf(req.getType()); } catch (Exception ignored) {}
        Screen s = Screen.builder().theatreId(theatreId).name(req.getName())
            .totalSeats(req.getTotalSeats()).type(type).build();
        return toScreenDto(screenRepo.save(s));
    }

    @Transactional
    public void deleteScreen(UUID screenId) {
        Screen s = screenRepo.findById(screenId).orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + screenId));
        s.setActive(false);
        screenRepo.save(s);
    }

    private Theatre findTheatre(UUID id) {
        return theatreRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre not found: " + id));
    }
    private TheatreResponse toDto(Theatre t) {
        return new TheatreResponse(t.getId(),t.getName(),t.getAddress(),t.getCity(),t.getState(),t.getPincode(),t.getPhone(),t.getEmail(),t.isActive());
    }
    private ScreenResponse toScreenDto(Screen s) {
        return new ScreenResponse(s.getId(),s.getTheatreId(),s.getName(),s.getTotalSeats(),s.getType(),s.isActive());
    }
}
