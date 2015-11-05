package org.asuki.common.lock;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class LockFreeSpaceship implements Spaceship {

    private final AtomicReference<Position> position = new AtomicReference<>(
            new Position(0, 0));

    @Override
    public int read(int[] coordinates) {
        final Position currentPosition = position.get();
        coordinates[0] = currentPosition.getX();
        coordinates[1] = currentPosition.getY();

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        int tries = 0;
        Position currentPosition;

        do {
            ++tries;
            currentPosition = position.get();
        } while (!position.compareAndSet(currentPosition,
                currentPosition.move(xDelta, yDelta)));

        return tries;
    }

    @AllArgsConstructor
    public static class Position {
        @Getter
        private int x;
        @Getter
        private int y;

        public Position move(int xDelta, int yDelta) {
            return new Position(x + xDelta, y + yDelta);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("x", this.position.get().getX())
                .add("y", this.position.get().getY()).toString();
    }
}
