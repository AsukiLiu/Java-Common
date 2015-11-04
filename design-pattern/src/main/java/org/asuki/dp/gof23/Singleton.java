package org.asuki.dp.gof23;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class Singleton {

    /*
     * Eager version
     */

    static final class Singleton1 implements Serializable {

        private static final long serialVersionUID = 1L;

        private Singleton1() {
        }

        private static final Singleton1 instance = new Singleton1();

        public static Singleton1 getInstance() {
            return instance;
        }

        private Object readResolve() {
            return instance;
        }
    }

    /*
     * Lazy version
     */

    static final class Singleton2 implements Serializable {

        private static final long serialVersionUID = 1L;

        private Singleton2() {
        }

        private static Singleton2 instance;

        public static synchronized Singleton2 getInstance() {
            if (instance == null) {
                instance = new Singleton2();
            }
            return instance;
        }
    }

    /*
     * Double check locking version
     */

    static final class Singleton3 {

        private Singleton3() {
            // Prevent instantiating by reflection
            if (instance != null) {
                throw new IllegalStateException("Already initialized.");
            }
        }

        private static volatile Singleton3 instance;

        public static Singleton3 getInstance() {
            Singleton3 local = instance;

            if (local == null) {
                synchronized (Singleton3.class) {
                    local = instance;
                    if (local == null) {
                        instance = local = new Singleton3();
                    }
                }
            }
            return local;
        }
    }

    /*
     * Inner class version (Recommendation)
     */

    static final class Singleton4 implements Serializable {

        private static final long serialVersionUID = 1L;

        private Singleton4() {
        }

        private static class SingletonHolder {
            private static final Singleton4 instance = new Singleton4();
        }

        public static Singleton4 getInstance() {
            return SingletonHolder.instance;
        }

        private Object readResolve() {
            return getInstance();
        }
    }

    /*
     * Enum version
     */

    public enum Singleton5 {
        INSTANCE;

        @Override
        public String toString() {
            return getDeclaringClass().getCanonicalName() + "@" + hashCode();
        }
    }

    /*
     * Not thread-safe version for testing
     */

    static class Singleton6 {

        private static Singleton6 instance;

        @SneakyThrows
        private Singleton6() {
            TimeUnit.MILLISECONDS.sleep(10);
        }

        public static Singleton6 getInstance() {
            if (instance == null) {
                instance = new Singleton6();
                out.println(currentThread().getName() + ": created " + instance);
            }
            return instance;
        }
    }
}
