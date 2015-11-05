package org.asuki.common.lock;

public interface Spaceship {

    int read(int[] coordinates);

    int write(int xDelta, int yDelta);
}
