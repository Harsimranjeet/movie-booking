package com.movie.booking.seat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;

/**
 * File-based distributed locking for seat reservation (demo replacement for Redis).
 * Each seat gets its own lock file. FileLock is JVM-level cross-process safe.
 */
@Slf4j
@Component
public class SeatLockService {

    private static final String LOCK_DIR = System.getProperty("java.io.tmpdir") + "/seat-locks/";

    public SeatLockService() {
        new File(LOCK_DIR).mkdirs();
    }

    /**
     * Acquires a file lock for the given seat. Returns null if already locked.
     * Caller must call release(lock) after the critical section.
     */
    public FileLock tryLock(UUID seatId) {
        try {
            File lockFile = new File(LOCK_DIR + seatId + ".lock");
            RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
            FileChannel channel = raf.getChannel();
            FileLock lock = channel.tryLock();  // non-blocking — returns null if locked
            if (lock == null) {
                raf.close();
            }
            return lock;
        } catch (Exception e) {
            log.warn("Could not acquire lock for seat {}: {}", seatId, e.getMessage());
            return null;
        }
    }

    public void release(FileLock lock) {
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
