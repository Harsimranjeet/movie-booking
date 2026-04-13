package com.movie.booking.show.service;
import com.movie.booking.show.dto.ShowDtos.*;
import com.movie.booking.show.exception.ResourceNotFoundException;
import com.movie.booking.show.model.Show;
import com.movie.booking.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class ShowService {

    private final ShowRepository repo;

    public ShowResponse getById(UUID id) {
        log.debug("Fetching show by id={}", id);
        return toDto(find(id));
    }

    public List<ShowResponse> getByMovieAndDate(UUID movieId, LocalDate date) {
        log.debug("Fetching shows for movieId={}, date={}", movieId, date);
        List<ShowResponse> shows = repo.findByMovieIdAndShowDate(movieId, date).stream().map(this::toDto).toList();
        log.debug("Found {} shows for movieId={} on {}", shows.size(), movieId, date);
        return shows;
    }

    public List<ShowResponse> getByTheatreAndDate(UUID theatreId, LocalDate date) {
        log.debug("Fetching shows for theatreId={}, date={}", theatreId, date);
        List<ShowResponse> shows = repo.findByTheatreIdAndShowDate(theatreId, date).stream().map(this::toDto).toList();
        log.debug("Found {} shows for theatreId={} on {}", shows.size(), theatreId, date);
        return shows;
    }

    public List<ShowResponse> getByMovieTheatreDate(UUID movieId, UUID theatreId, LocalDate date) {
        log.debug("Fetching shows for movieId={}, theatreId={}, date={}", movieId, theatreId, date);
        List<ShowResponse> shows = repo.findByMovieIdAndTheatreIdAndShowDate(movieId, theatreId, date).stream().map(this::toDto).toList();
        log.debug("Found {} shows for movieId={}, theatreId={} on {}", shows.size(), movieId, theatreId, date);
        return shows;
    }

    public List<ShowResponse> getByDate(LocalDate date) {
        log.debug("Fetching all OPEN shows on date={}", date);
        List<ShowResponse> shows = repo.findByShowDateAndStatus(date, Show.ShowStatus.OPEN).stream().map(this::toDto).toList();
        log.debug("Found {} OPEN shows on {}", shows.size(), date);
        return shows;
    }

    @Transactional
    public ShowResponse create(CreateShowRequest req) {
        log.info("Creating show: movieId={}, theatreId={}, screenId={}, date={}, time={}-{}",
            req.getMovieId(), req.getTheatreId(), req.getScreenId(),
            req.getShowDate(), req.getStartTime(), req.getEndTime());
        Show s = Show.builder()
            .movieId(req.getMovieId()).theatreId(req.getTheatreId()).screenId(req.getScreenId())
            .showDate(req.getShowDate()).startTime(req.getStartTime()).endTime(req.getEndTime())
            .language(req.getLanguage()).format(req.getFormat()).basePrice(req.getBasePrice())
            .status(Show.ShowStatus.OPEN).build();
        ShowResponse saved = toDto(repo.save(s));
        log.info("Show created: id={}, movieId={}, date={}", saved.getId(), saved.getMovieId(), saved.getShowDate());
        return saved;
    }

    @Transactional
    public ShowResponse update(UUID id, UpdateShowRequest req) {
        log.info("Updating show: id={}", id);
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
        ShowResponse updated = toDto(repo.save(s));
        log.info("Show updated: id={}, status={}", updated.getId(), updated.getStatus());
        return updated;
    }

    @Transactional
    public void cancel(UUID id) {
        log.info("Cancelling show: id={}", id);
        Show s = find(id);
        s.setStatus(Show.ShowStatus.CANCELLED);
        repo.save(s);
        log.info("Show cancelled: id={}", id);
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
