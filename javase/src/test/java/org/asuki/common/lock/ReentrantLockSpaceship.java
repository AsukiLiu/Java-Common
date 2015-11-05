package org.asuki.common.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Objects;

public class ReentrantLockSpaceship implements Spaceship {

    private final Lock lock = new ReentrantLock();

    private int x;
    private int y;

    @Override
    public int read(int[] coordinates) {
        try {
            lock.lock();

            coordinates[0] = x;
            coordinates[1] = y;
        } finally {
            lock.unlock();
        }

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        try {
            lock.lock();

            x += xDelta;
            y += yDelta;
        } finally {
            lock.unlock();
        }

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }
}
