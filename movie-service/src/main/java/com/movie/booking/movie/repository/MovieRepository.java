
package com.movie.booking.movie.repository;
import com.movie.booking.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    List<Movie> findByActiveTrueOrderByCreatedAtDesc();
    List<Movie> findByLanguageIgnoreCaseAndActiveTrue(String language);
    List<Movie> findByGenreIgnoreCaseAndActiveTrue(String genre);
    List<Movie> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
}
