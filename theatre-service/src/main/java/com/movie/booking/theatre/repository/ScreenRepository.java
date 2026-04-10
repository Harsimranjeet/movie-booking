
package com.movie.booking.theatre.repository;
import com.movie.booking.theatre.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, UUID> {
    List<Screen> findByTheatreIdAndActiveTrue(UUID theatreId);
}
