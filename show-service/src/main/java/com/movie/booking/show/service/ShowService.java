package com.movie.booking.show.service;
import com.movie.booking.show.dto.ShowDtos.*;
import com.movie.booking.show.exception.ResourceNotFoundException;
import com.movie.booking.show.model.Show;
import com.movie.booking.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ShowService {

    private final ShowRepository repo;

    public ShowResponse getById(UUID id) { return toDto(find(id)); }

    public List<ShowResponse> getByMovieAndDate(UUID movieId, LocalDate date) {
        return repo.findByMovieIdAndShowDate(movieId, date).stream().map(this::toDto).toList();
    }
    public List<ShowResponse> getByTheatreAndDate(UUID theatreId, LocalDate date) {
        return repo.findByTheatreIdAndShowDate(theatreId, date).stream().map(this::toDto).toList();
    }
    public List<ShowResponse> getByMovieTheatreDate(UUID movieId, UUID theatreId, LocalDate date) {
        return repo.findByMovieIdAndTheatreIdAndShowDate(movieId, theatreId, date).stream().map(this::toDto).toList();
    }
    public List<ShowResponse> getByDate(LocalDate date) {
        return repo.findByShowDateAndStatus(date, Show.ShowStatus.OPEN).stream().map(this::toDto).toList();
    }

    @Transactional
    public ShowResponse create(CreateShowRequest req) {
        Show s = Show.builder()
            .movieId(req.getMovieId()).theatreId(req.getTheatreId()).screenId(req.getScreenId())
            .showDate(req.getShowDate()).startTime(req.getStartTime()).endTime(req.getEndTime())
            .language(req.getLanguage()).format(req.getFormat()).basePrice(req.getBasePrice())
            .status(Show.ShowStatus.OPEN).build();
        return toDto(repo.save(s));
    }

    @Transactional
    public ShowResponse update(UUID id, UpdateShowRequest req) {
        Show s = find(id);
        if (req.getShowDate()  != null) s.setShowDate(req.getShowDate());
        if (req.getStartTime() != null) s.setStartTime(req.getStartTime());
        if (req.getEndTime()   != null) s.setEndTime(req.getEndTime());
        if (req.getLanguage()  != null) s.setLanguage(req.getLanguage());
        if (req.getFormat()    != null) s.setFormat(req.getFormat());
        if (req.getBasePrice() != null) s.setBasePrice(req.getBasePrice());
        if (req.getStatus()    != null) {
            try { s.setStatus(Show.ShowStatus.valueOf(req.getStatus())); } catch (Exception ignored) {}
        }
        return toDto(repo.save(s));
    }

    @Transactional
    public void cancel(UUID id) {
        Show s = find(id);
        s.setStatus(Show.ShowStatus.CANCELLED);
        repo.save(s);
    }

    private Show find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Show not found: " + id));
    }
    private ShowResponse toDto(Show s) {
        return new ShowResponse(s.getId(),s.getMovieId(),s.getTheatreId(),s.getScreenId(),
            s.getShowDate(),s.getStartTime(),s.getEndTime(),s.getLanguage(),s.getFormat(),
            s.getBasePrice(),s.getStatus());
    }
}
