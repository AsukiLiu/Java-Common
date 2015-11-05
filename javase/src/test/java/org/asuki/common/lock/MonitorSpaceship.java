package org.asuki.common.lock;

import com.google.common.base.Objects;
import com.google.common.util.concurrent.Monitor;

public class MonitorSpaceship implements Spaceship {

    private Monitor monitor = new Monitor();
    private Monitor.Guard guard = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return true;
        }
    };

    private int x;
    private int y;

    @Override
    public int read(int[] coordinates) {

        try {
            monitor.enterWhen(guard);

            coordinates[0] = x;
            coordinates[1] = y;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            monitor.leave();
        }

        return 1;
    }

    @Override
    public int write(int xDelta, int yDelta) {

        try {
            monitor.enterWhen(guard);

            x += xDelta;
            y += yDelta;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            monitor.leave();
        }

        return 1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("x", this.x).add("y", this.y)
                .toString();
    }

}
