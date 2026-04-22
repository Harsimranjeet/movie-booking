package com.movie.booking.booking.service;
import com.movie.booking.booking.client.SeatClient;
import com.movie.booking.booking.dto.BookingDtos.*;
import com.movie.booking.booking.exception.BadRequestException;
import com.movie.booking.booking.exception.ResourceNotFoundException;
import com.movie.booking.booking.model.Booking;
import com.movie.booking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repo;
    private final SeatClient seatClient;

    public BookingResponse getById(String id) {
        log.debug("Fetching booking by id={}", id);
        return toDto(find(UUID.fromString(id)));
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

    public List<BookingResponse> getByShow(String sid) {
        log.debug("Fetching bookings for showId={}", sid);
        List<BookingResponse> bookings = repo.findByShowId(UUID.fromString(sid)).stream().map(this::toDto).toList();
        log.debug("Found {} bookings for showId={}", bookings.size(), sid);
        return bookings;
    }

    @Transactional
    public BookingResponse create(UUID userId, CreateBookingRequest req) {
        String ref = "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Booking b = Booking.builder()
            .userId(userId).showId(req.getShowId())
            .seatIds(req.getSeatIds()).finalAmount(req.getFinalAmount())
            .status(Booking.BookingStatus.PENDING)
            .bookingRef(ref).build();

        Booking saved = repo.save(b);

        // Lock seats in seat-service — if this fails the @Transactional rolls back the booking insert
        seatClient.reserve(Map.of(
            "seatIds",     req.getSeatIds().stream().map(UUID::toString).toList(),
            "bookingId",   saved.getId().toString(),
            "lockMinutes", 10
        ));

        log.info("Booking created and seats locked: ref={}, user={}, seats={}", ref, userId, req.getSeatIds());
        return toDto(saved);
    }

    @Transactional
    public BookingResponse confirm(ConfirmBookingRequest req) {
        Booking b = find(req.getBookingId());
        if (b.getStatus() != Booking.BookingStatus.PENDING)
            throw new BadRequestException("Booking is not in PENDING state");
        b.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking saved = repo.save(b);

        // Mark seats as BOOKED in seat-service
        seatClient.confirm(saved.getId().toString());

        log.info("Booking confirmed: ref={}", b.getBookingRef());
        return toDto(saved);
    }

    @Transactional
    public BookingResponse cancel(UUID id, UUID userId) {
        Booking b = find(id);
        if (!b.getUserId().equals(userId))
            throw new BadRequestException("You can only cancel your own bookings");
        if (b.getStatus() == Booking.BookingStatus.CANCELLED)
            throw new BadRequestException("Booking is already cancelled");
        b.setStatus(Booking.BookingStatus.CANCELLED);
        Booking saved = repo.save(b);

        // Release seats back to AVAILABLE in seat-service
        seatClient.release(saved.getId().toString());

        log.info("Booking cancelled: ref={}", b.getBookingRef());
        return toDto(saved);
    }

    private Booking find(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }
    private BookingResponse toDto(Booking b) {
        return new BookingResponse(b.getId(), b.getUserId(), b.getShowId(),
            b.getSeatIds(), b.getFinalAmount(), b.getStatus(),
            b.getBookingRef(), b.getCreatedAt());
    }
}
