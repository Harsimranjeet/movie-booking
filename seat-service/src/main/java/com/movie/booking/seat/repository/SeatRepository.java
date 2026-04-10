package com.movie.booking.seat.repository;
import com.movie.booking.seat.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByShowId(UUID showId);
    List<Seat> findByShowIdAndStatus(UUID showId, Seat.SeatStatus status);
    List<Seat> findByShowIdAndCategory(UUID showId, Seat.SeatCategory category);

    @Modifying
    @Query("UPDATE Seat s SET s.status = 'AVAILABLE', s.lockedByBookingId = null, s.lockExpiresAt = null " +
           "WHERE s.status = 'LOCKED' AND s.lockExpiresAt < :now")
    int releaseExpiredLocks(Instant now);
}
