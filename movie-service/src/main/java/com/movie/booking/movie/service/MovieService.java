
package com.movie.booking.movie.service;
import com.movie.booking.movie.dto.MovieDtos.*;
import com.movie.booking.movie.exception.ResourceNotFoundException;
import com.movie.booking.movie.model.Movie;
import com.movie.booking.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repo;

    public List<MovieResponse> getAll() {
        log.debug("Fetching all active movies");
        List<MovieResponse> movies = repo.findByActiveTrueOrderByCreatedAtDesc().stream().map(this::toDto).toList();
        log.debug("Found {} active movies", movies.size());
        return movies;
    }

    public MovieResponse getById(String id) {
        log.debug("Fetching movie by id={}", id);
        return toDto(find(id));
    }

    public List<MovieResponse> getByLanguage(String lang) {
        log.debug("Fetching movies by language={}", lang);
        List<MovieResponse> movies = repo.findByLanguageIgnoreCaseAndActiveTrue(lang).stream().map(this::toDto).toList();
        log.debug("Found {} movies for language={}", movies.size(), lang);
        return movies;
    }

    public List<MovieResponse> getByGenre(String genre) {
        log.debug("Fetching movies by genre={}", genre);
        List<MovieResponse> movies = repo.findByGenreIgnoreCaseAndActiveTrue(genre).stream().map(this::toDto).toList();
        log.debug("Found {} movies for genre={}", movies.size(), genre);
        return movies;
    }

    public List<MovieResponse> search(String title) {
        log.debug("Searching movies with title containing '{}'", title);
        List<MovieResponse> results = repo.findByTitleContainingIgnoreCaseAndActiveTrue(title).stream().map(this::toDto).toList();
        log.debug("Search for '{}' returned {} result(s)", title, results.size());
        return results;
    }

    @Transactional
    public MovieResponse create(CreateMovieRequest req) {
        log.info("Creating movie: title='{}', language='{}', genre='{}'", req.getTitle(), req.getLanguage(), req.getGenre());
        Movie m = Movie.builder()
            .title(req.getTitle()).description(req.getDescription())
            .language(req.getLanguage()).genre(req.getGenre())
            .durationMins(req.getDurationMins()).posterUrl(req.getPosterUrl())
            .trailerUrl(req.getTrailerUrl()).certification(req.getCertification())
            .build();
        MovieResponse saved = toDto(repo.save(m));
        log.info("Movie created: id={}, title='{}'", saved.getId(), saved.getTitle());
        return saved;
    }

    @Transactional
    public MovieResponse update(UUID id, UpdateMovieRequest req) {
        log.info("Updating movie: id={}", id);
        Movie m = find(String.valueOf(id));
        if (req.getTitle() != null) m.setTitle(req.getTitle());

        if (req.getDescription() != null) m.setDescription(req.getDescription());

        if (req.getLanguage() != null) m.setLanguage(req.getLanguage());

        if (req.getGenre() != null) m.setGenre(req.getGenre());

        if (req.getDurationMins()  > 0) m.setDurationMins(req.getDurationMins());

        if (req.getPosterUrl() != null) m.setPosterUrl(req.getPosterUrl());

        if (req.getTrailerUrl() != null) m.setTrailerUrl(req.getTrailerUrl());

        if (req.getCertification() != null) m.setCertification(req.getCertification());

        if (req.getRating() != null) m.setRating(req.getRating());

        MovieResponse updated = toDto(repo.save(m));
        log.info("Movie updated: id={}, title='{}'", updated.getId(), updated.getTitle());
        return updated;
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Soft-deleting movie: id={}", id);
        Movie m = find(String.valueOf(id));
        m.setActive(false);
        repo.save(m);
        log.info("Movie deactivated: id={}, title='{}'", id, m.getTitle());
    }

    private Movie find(String id) {
        return repo.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
    }

    private MovieResponse toDto(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(),
            m.getLanguage(), m.getGenre(), m.getDurationMins(), m.getPosterUrl(),
            m.getTrailerUrl(), m.getCertification(), m.getRating(), m.isActive(), m.getCreatedAt());
    }
}
