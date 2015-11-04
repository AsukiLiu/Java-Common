package org.asuki.dp.gof23;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lombok.Cleanup;
import lombok.SneakyThrows;

import org.asuki.dp.gof23.Singleton.Singleton1;
import org.asuki.dp.gof23.Singleton.Singleton2;
import org.asuki.dp.gof23.Singleton.Singleton3;
import org.asuki.dp.gof23.Singleton.Singleton4;
import org.asuki.dp.gof23.Singleton.Singleton5;
import org.asuki.dp.gof23.Singleton.Singleton6;
import org.testng.annotations.Test;

public class SingletonTest {

    @Test
    public void testEagerVersion() {
        Singleton1 s1 = Singleton1.getInstance();
        Singleton1 s2 = Singleton1.getInstance();
        Singleton1 s3 = (Singleton1) deepCopy(Singleton1.getInstance());

        assertThat(s1 == s2, is(true));
        // Implement readResolve()
        assertThat(s1 == s3, is(true));
    }

    @Test
    public void testLazyVersion() {
        Singleton2 s1 = Singleton2.getInstance();
        Singleton2 s2 = Singleton2.getInstance();
        Singleton2 s3 = (Singleton2) deepCopy(Singleton2.getInstance());

        assertThat(s1 == s2, is(true));
        // Not implement readResolve()
        assertThat(s1 == s3, is(false));
    }

    @Test
    public void testDoubleCheckLockingVersion() {
        Singleton3 s1 = Singleton3.getInstance();
        Singleton3 s2 = Singleton3.getInstance();

        assertThat(s1 == s2, is(true));
    }

    @Test
    public void testInnerClassVersion() {
        Singleton4 s1 = Singleton4.getInstance();
        Singleton4 s2 = Singleton4.getInstance();

        assertThat(s1 == s2, is(true));
    }

    @Test
    public void testEnumVersion() {
        Singleton5 s1 = Singleton5.INSTANCE;
        Singleton5 s2 = Singleton5.INSTANCE;

        assertThat(s1 == s2, is(true));
        assertThat(s1.toString(), is(s2.toString()));
    }

    @SneakyThrows
    private static Object deepCopy(Object obj) {
        @Cleanup
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        @Cleanup
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(obj);

        @Cleanup
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        @Cleanup
        ObjectInputStream ois = new ObjectInputStream(bis);

        return ois.readObject();
    }

    private static final int NUM_THREADS = 10;

    @Test
    public void testThreadSafe() {
        Runnable task = () -> {
            Singleton6 instance = Singleton6.getInstance();
            out.println(currentThread().getName() + ": got " + instance);
        };

        for (int i = 0; i < NUM_THREADS; i++) {
            Thread thread = new Thread(task);
            thread.start();
        }

    }

}
