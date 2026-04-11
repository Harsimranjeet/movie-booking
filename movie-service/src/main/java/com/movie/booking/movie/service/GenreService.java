
package com.movie.booking.movie.service;
import com.movie.booking.movie.model.Genre;
import com.movie.booking.movie.repository.GenreRepository;
import com.movie.booking.movie.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository repo;

    public List<Genre> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Genre create(String name, String description) {
        return repo.save(Genre.builder().name(name).description(description).build());
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Genre not found");
        repo.deleteById(id);
    }
}
