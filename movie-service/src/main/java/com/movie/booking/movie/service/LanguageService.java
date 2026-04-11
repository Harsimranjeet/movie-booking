
package com.movie.booking.movie.service;
import com.movie.booking.movie.model.Language;
import com.movie.booking.movie.repository.LanguageRepository;
import com.movie.booking.movie.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository repo;

    public List<Language> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Language create(String code, String name) {
        return repo.save(Language.builder().code(code).name(name).build());
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Language not found");
        repo.deleteById(id);
    }
}
