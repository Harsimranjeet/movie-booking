
package com.movie.booking.movie.repository;
import com.movie.booking.movie.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {}
