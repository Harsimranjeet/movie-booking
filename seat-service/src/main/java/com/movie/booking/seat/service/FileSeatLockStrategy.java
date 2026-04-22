package com.movie.booking.seat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * File-based seat locking (default strategy).
 * Each seat gets its own lock file. FileLock is JVM-level cross-process safe.
 *
 * Limitation: only works within a single JVM instance.
 * For multi-instance deployments, switch to RedisSeatLockStrategy.
 *
 * Activated when: seat.lock.strategy=file  (or property is absent — default)
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "seat.lock.strategy", havingValue = "file", matchIfMissing = true)
public class FileSeatLockStrategy implements SeatLockStrategy {

    private static final String LOCK_DIR = System.getProperty("java.io.tmpdir") + "/seat-locks/";

    // Holds active FileLocks keyed by seatId so they can be released by seatId
    private final ConcurrentHashMap<UUID, FileLock> activeLocks = new ConcurrentHashMap<>();

    public FileSeatLockStrategy() {
        new File(LOCK_DIR).mkdirs();
    }

    @Override
    public boolean tryLock(UUID seatId, UUID bookingId) {
        FileLock lock = acquireFileLock(seatId);
        if (lock == null) {
            log.warn("Could not acquire file lock for seatId={}", seatId);
            return false;
        }
        activeLocks.put(seatId, lock);
        return true;
    }

    @Override
    public void release(UUID seatId) {
        FileLock lock = activeLocks.remove(seatId);
        releaseFileLock(lock);
        log.debug("Released file lock for seatId={}", seatId);
    }

    // ── file lock mechanics ───────────────────────────────────────────────────

    private FileLock acquireFileLock(UUID seatId) {
        try {
            File lockFile = new File(LOCK_DIR + seatId + ".lock");
            RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            FileLock lock = channel.tryLock(); // non-blocking — returns null if already locked
            if (lock == null) {
                randomAccessFile.close();
            }
            return lock;
        } catch (Exception e) {
            log.warn("Could not acquire lock for seat {}: {}", seatId, e.getMessage());
            return null;
        }
    }

    private void releaseFileLock(FileLock lock) {
        if (lock != null) {
            try {
                lock.channel().close();
                lock.release();
            } catch (Exception e) {
                log.warn("Could not release lock: {}", e.getMessage());
            }
        }
    }
}
