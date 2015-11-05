package org.asuki.common.lock;

import java.util.concurrent.Semaphore;

import com.google.common.base.Objects;

public class SemaphoreSpaceShip implements Spaceship {

    private final Semaphore semaphore;

    private int x;
    private int y;

    public SemaphoreSpaceShip() {
        semaphore = new Semaphore(1);
    }

    @Override
    public int read(int[] coordinates) {
        try {
            semaphore.acquire();

            coordinates[0] = x;
            coordinates[1] = y;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        try {
            semaphore.acquire();

            x += xDelta;
            y += yDelta;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }
}
