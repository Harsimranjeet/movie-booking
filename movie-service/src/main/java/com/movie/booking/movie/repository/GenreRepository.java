
package com.movie.booking.movie.repository;
import com.movie.booking.movie.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {}
