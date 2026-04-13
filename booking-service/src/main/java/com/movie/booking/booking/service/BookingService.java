package com.movie.booking.booking.service;
import com.movie.booking.booking.dto.BookingDtos.*;
import com.movie.booking.booking.exception.BadRequestException;
import com.movie.booking.booking.exception.ResourceNotFoundException;
import com.movie.booking.booking.model.Booking;
import com.movie.booking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repo;

    public BookingResponse getById(UUID id) {
        log.debug("Fetching booking by id={}", id);
        return toDto(find(id));
    }

    public BookingResponse getByRef(String ref) {
        log.debug("Fetching booking by ref='{}'", ref);
        return toDto(repo.findByBookingRef(ref).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + ref)));
    }

    public List<BookingResponse> getByUser(UUID uid) {
        log.debug("Fetching bookings for userId={}", uid);
        List<BookingResponse> bookings = repo.findByUserIdOrderByCreatedAtDesc(uid).stream().map(this::toDto).toList();
        log.debug("Found {} bookings for userId={}", bookings.size(), uid);
        return bookings;
    }

    public List<BookingResponse> getByShow(UUID sid) {
        log.debug("Fetching bookings for showId={}", sid);
        List<BookingResponse> bookings = repo.findByShowId(sid).stream().map(this::toDto).toList();
        log.debug("Found {} bookings for showId={}", bookings.size(), sid);
        return bookings;
    }

    @Transactional
    public BookingResponse create(UUID userId, CreateBookingRequest req) {
        if (req.getSeatIds().size() != req.getTicketCount())
            throw new BadRequestException("Seat count does not match ticket count");

        double finalAmount = req.getTotalAmount();
        double discountAmount = 0;
        // Offer discount is pre-calculated by the client via offer-service
        // Here we just record what was agreed

        String ref = "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Booking b = Booking.builder()
            .userId(userId).showId(req.getShowId())
            .theatreId(req.getTheatreId()).movieId(req.getMovieId())
            .seatIds(req.getSeatIds()).ticketCount(req.getTicketCount())
            .totalAmount(req.getTotalAmount())
            .discountAmount(discountAmount).finalAmount(finalAmount)
            .offerCode(req.getOfferCode())
            .status(Booking.BookingStatus.PENDING)
            .bookingRef(ref).build();

        Booking saved = repo.save(b);
        log.info("Booking created: ref={}, user={}", ref, userId);
        return toDto(saved);
    }

    @Transactional
    public BookingResponse confirm(ConfirmBookingRequest req) {
        Booking b = find(req.getBookingId());
        if (b.getStatus() != Booking.BookingStatus.PENDING)
            throw new BadRequestException("Booking is not in PENDING state");
        b.setStatus(Booking.BookingStatus.CONFIRMED);
        b.setConfirmedAt(Instant.now());
        log.info("Booking confirmed: ref={}", b.getBookingRef());
        return toDto(repo.save(b));
    }

    @Transactional
    public BookingResponse cancel(UUID id, UUID userId) {
        Booking b = find(id);
        if (!b.getUserId().equals(userId))
            throw new BadRequestException("You can only cancel your own bookings");
        if (b.getStatus() == Booking.BookingStatus.CANCELLED)
            throw new BadRequestException("Booking is already cancelled");
        b.setStatus(Booking.BookingStatus.CANCELLED);
        b.setCancelledAt(Instant.now());
        log.info("Booking cancelled: ref={}", b.getBookingRef());
        return toDto(repo.save(b));
    }

    private Booking find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }
    private BookingResponse toDto(Booking b) {
        return new BookingResponse(b.getId(),b.getUserId(),b.getShowId(),b.getTheatreId(),
            b.getMovieId(),b.getSeatIds(),b.getTicketCount(),b.getTotalAmount(),
            b.getDiscountAmount(),b.getFinalAmount(),b.getOfferCode(),b.getStatus(),
            b.getBookingRef(),b.getCreatedAt(),b.getConfirmedAt(),b.getCancelledAt());
    }
}
