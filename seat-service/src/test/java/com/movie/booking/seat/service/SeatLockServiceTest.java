package com.movie.booking.seat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatLockServiceTest {

    // ══════════════════════════════════════════════════════════════════════════
    // File-based strategy tests  (no external infra needed)
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void file_lockAndRelease_shouldWork() {
        SeatLockStrategy strategy = new FileSeatLockStrategy();
        UUID seatId    = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        assertTrue(strategy.tryLock(seatId, bookingId), "Should acquire lock on a fresh seat");

        strategy.release(seatId);

        // After release, same seat must be lockable again
        assertTrue(strategy.tryLock(seatId, bookingId), "Should re-acquire after release");
        strategy.release(seatId);
    }

    @Test
    void file_concurrentRequests_onlyOneWins() throws InterruptedException {
        SeatLockStrategy strategy = new FileSeatLockStrategy();
        UUID seatId = UUID.randomUUID();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount    = new AtomicInteger(0);
        CountDownLatch startLatch  = new CountDownLatch(1);
        CountDownLatch doneLatch   = new CountDownLatch(2);

        Runnable task = () -> {
            try {
                startLatch.await(); // both threads released simultaneously
                boolean locked = strategy.tryLock(seatId, UUID.randomUUID());
                if (locked) {
                    successCount.incrementAndGet();
                    Thread.sleep(100); // hold the lock briefly
                    strategy.release(seatId);
                } else {
                    failCount.incrementAndGet();
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        startLatch.countDown(); // fire both at the same time
        doneLatch.await();

        assertEquals(1, successCount.get(), "Exactly ONE thread should acquire the lock");
        assertEquals(1, failCount.get(),    "Exactly ONE thread should be rejected");
    }

    @Test
    void file_differentSeats_lockIndependently() {
        SeatLockStrategy strategy = new FileSeatLockStrategy();
        UUID seat1 = UUID.randomUUID();
        UUID seat2 = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        assertTrue(strategy.tryLock(seat1, bookingId), "Seat1 should be lockable");
        assertTrue(strategy.tryLock(seat2, bookingId), "Seat2 should lock independently");

        strategy.release(seat1);
        strategy.release(seat2);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Redis-based strategy tests  (Redis is mocked — no real Redis needed)
    // ══════════════════════════════════════════════════════════════════════════

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Test
    void redis_tryLock_returnsTrueWhenKeyNotExists() {
        when(redis.opsForValue()).thenReturn(valueOps);
        SeatLockStrategy strategy = new RedisSeatLockStrategy(redis);

        UUID seatId    = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        // Redis SET NX returns true → key was absent, lock acquired
        when(valueOps.setIfAbsent(eq("seat:lock:" + seatId), eq(bookingId.toString()), any(Duration.class)))
            .thenReturn(true);

        assertTrue(strategy.tryLock(seatId, bookingId));
        verify(valueOps).setIfAbsent(eq("seat:lock:" + seatId), eq(bookingId.toString()), any(Duration.class));
    }

    @Test
    void redis_tryLock_returnsFalseWhenAlreadyLocked() {
        when(redis.opsForValue()).thenReturn(valueOps);
        SeatLockStrategy strategy = new RedisSeatLockStrategy(redis);

        UUID seatId    = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        // Redis SET NX returns false → key already exists, seat locked by someone else
        when(valueOps.setIfAbsent(eq("seat:lock:" + seatId), eq(bookingId.toString()), any(Duration.class)))
            .thenReturn(false);

        assertFalse(strategy.tryLock(seatId, bookingId));
    }

    @Test
    void redis_release_deletesRedisKey() {
        when(redis.opsForValue()).thenReturn(valueOps);
        SeatLockStrategy strategy = new RedisSeatLockStrategy(redis);

        UUID seatId = UUID.randomUUID();
        strategy.release(seatId);

        verify(redis).delete("seat:lock:" + seatId);
    }
}
