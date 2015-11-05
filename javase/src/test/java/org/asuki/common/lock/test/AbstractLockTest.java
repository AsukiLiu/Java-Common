package org.asuki.common.lock.test;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;

import java.util.Arrays;

import org.asuki.common.lock.Spaceship;
import org.testng.annotations.Test;

public abstract class AbstractLockTest {

    protected static final String expected = "{x=10, y=20}";

    protected Spaceship spaceship;

    @Test(timeOut = 1000, invocationCount = 10, threadPoolSize = 5)
    public void testThreads() {
        readAndWrite();
    }

    private void readAndWrite() {
        spaceship.write(1, 2);

        int[] currentCoordinates = { 0, 0 };
        spaceship.read(currentCoordinates);

        out.printf("%n Thread Id : %s %s %s", currentThread().getId(), Arrays
                .toString(currentCoordinates), spaceship.getClass()
                .getSimpleName());
    }

}
