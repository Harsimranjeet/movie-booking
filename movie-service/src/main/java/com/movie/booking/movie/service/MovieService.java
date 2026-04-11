
package com.movie.booking.movie.service;
import com.movie.booking.movie.dto.MovieDtos.*;
import com.movie.booking.movie.exception.ResourceNotFoundException;
import com.movie.booking.movie.model.Movie;
import com.movie.booking.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repo;

    public List<MovieResponse> getAll() {
        return repo.findByActiveTrueOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public MovieResponse getById(String id) {
        return toDto(find(id));
    }

    public List<MovieResponse> getByLanguage(String lang) {
        return repo.findByLanguageIgnoreCaseAndActiveTrue(lang).stream().map(this::toDto).toList();
    }

    public List<MovieResponse> getByGenre(String genre) {
        return repo.findByGenreIgnoreCaseAndActiveTrue(genre).stream().map(this::toDto).toList();
    }

    public List<MovieResponse> search(String title) {
        return repo.findByTitleContainingIgnoreCaseAndActiveTrue(title).stream().map(this::toDto).toList();
    }

    @Transactional
    public MovieResponse create(CreateMovieRequest req) {
        Movie m = Movie.builder()
            .title(req.getTitle()).description(req.getDescription())
            .language(req.getLanguage()).genre(req.getGenre())
            .durationMins(req.getDurationMins()).posterUrl(req.getPosterUrl())
            .trailerUrl(req.getTrailerUrl()).certification(req.getCertification())
            .build();
        return toDto(repo.save(m));
    }

    @Transactional
    public MovieResponse update(UUID id, UpdateMovieRequest req) {
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

        return toDto(repo.save(m));
    }

    @Transactional
    public void delete(UUID id) {
        Movie m = find(String.valueOf(id));
        m.setActive(false);
        repo.save(m);
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
