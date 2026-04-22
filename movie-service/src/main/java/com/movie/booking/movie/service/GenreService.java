
package com.movie.booking.movie.service;
import com.movie.booking.movie.model.Genre;
import com.movie.booking.movie.repository.GenreRepository;
import com.movie.booking.movie.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository repo;

    public List<Genre> getAll() {
        log.debug("Fetching all genres");
        List<Genre> genres = repo.findAll();
        log.debug("Found {} genres", genres.size());
        return genres;
    }

    @Transactional
    public Genre create(String name) {
        log.info("Creating genre: name='{}'", name);
        Genre saved = repo.save(Genre.builder().name(name).build());
        log.info("Genre created: id={}, name='{}'", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void delete(String id) {
        log.info("Deleting genre: id={}", id);
        if (!repo.existsById(UUID.fromString(id))) throw new ResourceNotFoundException("Genre not found");
        repo.deleteById(UUID.fromString(id));
        log.info("Genre deleted: id={}", id);
    }
}
