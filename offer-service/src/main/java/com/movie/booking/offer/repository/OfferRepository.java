package com.movie.booking.offer.repository;
import com.movie.booking.offer.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    Optional<Offer> findByCodeIgnoreCase(String code);
    List<Offer> findByActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(LocalDate from, LocalDate to);
}
