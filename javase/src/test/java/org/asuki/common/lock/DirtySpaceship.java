package org.asuki.common.lock;

import com.google.common.base.Objects;

public class DirtySpaceship implements Spaceship {

    private int x;
    private int y;

    @Override
    public int read(int[] coordinates) {
        coordinates[0] = x;
        coordinates[1] = y;

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        x += xDelta;
        y += yDelta;

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }
}
