package org.asuki.common.lock;

import java.util.concurrent.locks.StampedLock;

import com.google.common.base.Objects;

public class StampedLockSpaceship implements Spaceship {

    private final StampedLock lock = new StampedLock();

    private int x;
    private int y;

    @Override
    public int read(int[] coordinates) {

        int tries = 1;
        long stamp = lock.tryOptimisticRead();

        coordinates[0] = x;
        coordinates[1] = y;

        if (!lock.validate(stamp)) {
            ++tries;

            try {
                stamp = lock.readLock();

                coordinates[0] = x;
                coordinates[1] = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return tries;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        final long stamp = lock.writeLock();
        try {
            x += xDelta;
            y += yDelta;
        } finally {
            lock.unlockWrite(stamp);
        }

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }
}
