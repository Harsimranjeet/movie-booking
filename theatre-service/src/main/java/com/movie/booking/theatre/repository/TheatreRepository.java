
package com.movie.booking.theatre.repository;
import com.movie.booking.theatre.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, UUID> {
    List<Theatre> findByActiveTrueOrderByNameAsc();
    List<Theatre> findByCityIgnoreCaseAndActiveTrue(String city);
}
