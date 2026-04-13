
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
    public Language create(String code, String name) {
        log.info("Creating language: code='{}', name='{}'", code, name);
        Language saved = repo.save(Language.builder().code(code).name(name).build());
        log.info("Language created: id={}, code='{}'", saved.getId(), saved.getCode());
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting language: id={}", id);
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Language not found");
        repo.deleteById(id);
        log.info("Language deleted: id={}", id);
    }
}
