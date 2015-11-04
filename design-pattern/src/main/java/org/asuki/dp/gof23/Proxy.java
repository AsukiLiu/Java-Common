package org.asuki.dp.gof23;

import static java.lang.System.err;
import static java.lang.System.out;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class Proxy {

    interface Subject {
        void request();
    }

    static class RealSubject implements Subject {
        public RealSubject() {
            heavyInit();
        }

        @Override
        public void request() {
            out.println(this.getClass().getSimpleName());
        }

        @SneakyThrows
        private void heavyInit() {
            TimeUnit.SECONDS.sleep(2);
        }
    }

    static class ProxySubject extends RealSubject {
        private static final int MAX_NUM_ALLOWED = 3;

        private int count;

        @Override
        public void request() {
            if (count < MAX_NUM_ALLOWED) {
                super.request();
                count++;
            } else {
                err.println("Exceeding max number");
            }
        }
    }

    /*
     * Static Proxy
     */

    static class StaticProxySubject implements Subject {
        private Subject subject;

        @Override
        public void request() {
            if (this.subject == null) {
                this.subject = new RealSubject();
            }
            this.subject.request();
        }
    }

    /*
     * Dynamic Proxy
     */

    static class DynamicProxySubject implements InvocationHandler {
        private Subject subject;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            if (this.subject == null) {
                this.subject = new RealSubject();
            }

            this.subject.request();

            return null;
        }

        public static Subject createProxy() {
            return (Subject) java.lang.reflect.Proxy.newProxyInstance(
                    ClassLoader.getSystemClassLoader(),
                    new Class[] { Subject.class }, new DynamicProxySubject());
        }
    }

}
