package com.movie.booking.show.repository;
import com.movie.booking.show.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID> {
    List<Show> findByMovieIdAndShowDate(UUID movieId, LocalDate date);
    List<Show> findByTheatreIdAndShowDate(UUID theatreId, LocalDate date);
    List<Show> findByMovieIdAndTheatreIdAndShowDate(UUID movieId, UUID theatreId, LocalDate date);
    List<Show> findByShowDateAndStatus(LocalDate date, Show.ShowStatus status);
}
