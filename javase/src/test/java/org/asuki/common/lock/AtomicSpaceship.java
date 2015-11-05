package org.asuki.common.lock;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Objects;

public class AtomicSpaceship implements Spaceship {

    private final AtomicInteger x = new AtomicInteger();
    private final AtomicInteger y = new AtomicInteger();

    @Override
    public int read(int[] coordinates) {
        coordinates[0] = x.get();
        coordinates[1] = y.get();

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        x.addAndGet(xDelta);
        y.addAndGet(yDelta);

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x.get())
                .add("y", this.y.get()).toString();
    }
}
