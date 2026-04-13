package com.movie.booking.seat.service;
import com.movie.booking.seat.exception.BadRequestException;
import com.movie.booking.seat.exception.ResourceNotFoundException;
import com.movie.booking.seat.model.Seat;
import com.movie.booking.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.channels.FileLock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository repo;
    private final SeatLockService lockService;


    public List<Seat> getByShow(UUID showId) {
        log.debug("Fetching all seats for showId={}", showId);
        List<Seat> seats = repo.findByShowId(showId);
        log.debug("Found {} seats for showId={}", seats.size(), showId);
        return seats;
    }

    public List<Seat> getAvailableByShow(UUID showId) {
        log.debug("Fetching available seats for showId={}", showId);
        List<Seat> seats = repo.findByShowIdAndStatus(showId, Seat.SeatStatus.AVAILABLE);
        log.debug("Found {} available seats for showId={}", seats.size(), showId);
        return seats;
    }

    public List<Seat> getByShowAndCategory(UUID showId, String cat) {
        log.debug("Fetching seats for showId={}, category='{}'", showId, cat);
        Seat.SeatCategory c = Seat.SeatCategory.valueOf(cat.toUpperCase());
        List<Seat> seats = repo.findByShowIdAndCategory(showId, c);
        log.debug("Found {} seats for showId={}, category='{}'", seats.size(), showId, cat);
        return seats;
    }

    // ── Bulk create seats for a show ──────────────────────────────────────────

    @Transactional
    public List<Seat> createSeatsForShow(UUID showId, UUID screenId,
                                          List<SeatCreateRequest> requests) {
        List<Seat> seats = requests.stream().map(r ->
            Seat.builder()
                .showId(showId).screenId(screenId)
                .seatNumber(r.seatNumber()).rowLabel(r.rowLabel())
                .category(r.category()).price(r.price()).build()
        ).toList();
        return repo.saveAll(seats);
    }

    // ── Reserve (lock) seats — file-based locking ─────────────────────────────

    @Transactional
    public List<Seat> reserveSeats(List<UUID> seatIds, UUID bookingId, int lockMinutes) {
        log.info("Reserving seats: bookingId={}, seatCount={}, lockMinutes={}", bookingId, seatIds.size(), lockMinutes);
        List<FileLock> acquiredLocks = new ArrayList<>();
        List<Seat> seats = new ArrayList<>();

        try {
            for (UUID seatId : seatIds) {
                FileLock lock = lockService.tryLock(seatId);
                if (lock == null) {
                    log.warn("Seat reservation failed — concurrent request in progress: seatId={}, bookingId={}", seatId, bookingId);
                    throw new BadRequestException("Seat " + seatId + " is being processed by another request");
                }
                acquiredLocks.add(lock);

                Seat seat = repo.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));
                if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
                    log.warn("Seat reservation failed — seat not available: seatId={}, seatNumber='{}', currentStatus={}, bookingId={}", seatId, seat.getSeatNumber(), seat.getStatus(), bookingId);
                    throw new BadRequestException("Seat " + seat.getSeatNumber() + " is not available");
                }

                seat.setStatus(Seat.SeatStatus.LOCKED);
                seat.setLockedByBookingId(bookingId);
                seat.setLockExpiresAt(Instant.now().plusSeconds(lockMinutes * 60L));
                seats.add(repo.save(seat));
                log.debug("Seat locked: seatId={}, seatNumber='{}', bookingId={}, expiresAt={}", seatId, seat.getSeatNumber(), bookingId, seat.getLockExpiresAt());
            }
            log.info("Seats reserved successfully: bookingId={}, seatIds={}", bookingId, seatIds);
            return seats;
        } finally {
            acquiredLocks.forEach(lockService::release);
        }
    }

    // ── Confirm — LOCKED → BOOKED ─────────────────────────────────────────────

    @Transactional
    public void confirmSeats(UUID bookingId) {
        repo.findByShowId(bookingId).stream()
            .filter(s -> bookingId.equals(s.getLockedByBookingId()))
            .forEach(s -> {
                s.setStatus(Seat.SeatStatus.BOOKED);
                s.setLockExpiresAt(null);
                repo.save(s);
            });
    }

    // ── Release — LOCKED/BOOKED → AVAILABLE ──────────────────────────────────

    @Transactional
    public void releaseSeats(UUID bookingId) {
        repo.findAll().stream()
            .filter(s -> bookingId.equals(s.getLockedByBookingId()))
            .forEach(s -> {
                s.setStatus(Seat.SeatStatus.AVAILABLE);
                s.setLockedByBookingId(null);
                s.setLockExpiresAt(null);
                repo.save(s);
            });
    }

    // ── TTL cleanup — runs every 60 seconds ───────────────────────────────────

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void releaseExpiredLocks() {
        int released = repo.releaseExpiredLocks(Instant.now());
        if (released > 0) log.info("Released {} expired seat locks", released);
    }

    public record SeatCreateRequest(String seatNumber, String rowLabel,
                                    Seat.SeatCategory category, double price) {}
}
