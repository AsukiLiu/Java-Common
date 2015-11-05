package org.asuki.common.lock.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.asuki.common.lock.StampedLockSpaceship;
import org.testng.annotations.Test;

public class StampedLockTest extends AbstractLockTest {

    {
        spaceship = new StampedLockSpaceship();
    }

    @Test(dependsOnMethods = "testThreads")
    public void testResult() {
        assertThat(spaceship.toString(), containsString(expected));
    }
}
