package org.asuki.common.lock.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.asuki.common.lock.MonitorSpaceship;
import org.testng.annotations.Test;

public class MonitorTest extends AbstractLockTest {

    {
        spaceship = new MonitorSpaceship();
    }

    @Test(dependsOnMethods = "testThreads")
    public void testResult() {
        assertThat(spaceship.toString(), containsString(expected));
    }
}
