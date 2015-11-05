package org.asuki.common.lock.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.asuki.common.lock.ReentrantLockSpaceship;
import org.testng.annotations.Test;

public class ReentrantLockTest extends AbstractLockTest {

    {
        spaceship = new ReentrantLockSpaceship();
    }

    @Test(dependsOnMethods = "testThreads")
    public void testResult() {
        assertThat(spaceship.toString(), containsString(expected));
    }

}