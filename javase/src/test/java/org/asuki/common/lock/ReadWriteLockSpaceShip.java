package org.asuki.common.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.base.Objects;

public class ReadWriteLockSpaceShip implements Spaceship {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private int x;
    private int y;

    @Override
    public int read(int[] coordinates) {
        try {
            readLock.lock();

            coordinates[0] = x;
            coordinates[1] = y;
        } finally {
            readLock.unlock();
        }

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        try {
            writeLock.lock();

            x += xDelta;
            y += yDelta;
        } finally {
            writeLock.unlock();
        }

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }
}
