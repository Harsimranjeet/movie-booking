package com.movie.booking.booking.repository;
import com.movie.booking.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Booking> findByShowId(UUID showId);
    Optional<Booking> findByBookingRef(String ref);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
