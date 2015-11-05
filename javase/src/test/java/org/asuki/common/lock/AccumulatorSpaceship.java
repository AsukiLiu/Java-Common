package org.asuki.common.lock;

import java.util.concurrent.atomic.LongAccumulator;

import com.google.common.base.Objects;

public class AccumulatorSpaceship implements Spaceship {

    private final LongAccumulator x = new LongAccumulator((x, y) -> x + y, 0L);
    private final LongAccumulator y = new LongAccumulator((x, y) -> x + y, 0L);

    @Override
    public int read(int[] coordinates) {
        // coordinates[0] = x.intValue();
        // coordinates[1] = y.intValue();

        coordinates[0] = (int) x.get();
        coordinates[1] = (int) y.get();

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {
        x.accumulate(xDelta);
        y.accumulate(yDelta);

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x.intValue())
                .add("y", this.y.intValue()).toString();
    }
}
