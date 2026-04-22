package com.movie.booking.seat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis-based distributed seat locking.
 * Works correctly across multiple instances of seat-service.
 *
 * Key format : seat:lock:<seatId>
 * Value      : bookingId (for auditability)
 * TTL        : 30 seconds — safety net if service crashes before releasing
 *
 * Activated when: seat.lock.strategy=redis
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "seat.lock.strategy", havingValue = "redis")
public class RedisSeatLockStrategy implements SeatLockStrategy {

    private static final String   LOCK_PREFIX = "seat:lock:";
    private static final Duration LOCK_TTL = Duration.ofSeconds(30);

    private final StringRedisTemplate redis;

    /**
     * SET seat:lock:<seatId> <bookingId> NX EX 30
     * NX = only set if not exists (atomic — Redis guarantees only one caller wins)
     * EX = auto-expire in 30s (prevents stale locks if service crashes)
     */
    @Override
    public boolean tryLock(UUID seatId, UUID bookingId) {
        String key = LOCK_PREFIX + seatId;
        String value = bookingId.toString();
        Boolean acquired = redis.opsForValue().setIfAbsent(key, value, LOCK_TTL);
        boolean result = Boolean.TRUE.equals(acquired);
        if (!result) {
            log.warn("Could not acquire Redis lock for seatId={} — already locked", seatId);
        }
        return result;
    }

    @Override
    public void release(UUID seatId) {
        redis.delete(LOCK_PREFIX + seatId);
        log.debug("Released Redis lock for seatId={}", seatId);
    }
}
