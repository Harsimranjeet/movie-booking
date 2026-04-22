package com.movie.booking.seat.service;

import java.util.UUID;

/**
 * Strategy interface for distributed seat locking.
 *
 * Two implementations:
 *  - FileSeatLockStrategy  (default) — file-based, single JVM only
 *  - RedisSeatLockStrategy           — Redis SET NX EX, works across multiple instances
 *
 * Switch via: seat.lock.strategy=redis (default: file)
 */
public interface SeatLockStrategy {

    /**
     * Atomically acquires a lock for the seat.
     * @return true if lock acquired, false if already locked by another request
     */
    boolean tryLock(UUID seatId, UUID bookingId);

    /**
     * Releases the lock for the seat.
     */
    void release(UUID seatId);
}
