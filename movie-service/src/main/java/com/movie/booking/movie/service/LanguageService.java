
package com.movie.booking.movie.service;
import com.movie.booking.movie.model.Language;
import com.movie.booking.movie.repository.LanguageRepository;
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
public class LanguageService {

    private final LanguageRepository repo;

    public List<Language> getAll() {
        log.debug("Fetching all languages");
        List<Language> languages = repo.findAll();
        log.debug("Found {} languages", languages.size());
        return languages;
    }

    @Transactional
    public Language create(String name) {
        log.info("Creating language: name='{}'", name);
        Language saved = repo.save(Language.builder().name(name).build());
        log.info("Language created: id={}, name='{}'", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void delete(String id) {
        log.info("Deleting language: id={}", id);
        if (!repo.existsById(UUID.fromString(id))) throw new ResourceNotFoundException("Language not found");
        repo.deleteById(UUID.fromString(id));
        log.info("Language deleted: id={}", id);
    }
}
