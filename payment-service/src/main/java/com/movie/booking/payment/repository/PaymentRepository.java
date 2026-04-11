package com.movie.booking.payment.repository;
import com.movie.booking.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);
    List<Payment> findByUserId(UUID userId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
