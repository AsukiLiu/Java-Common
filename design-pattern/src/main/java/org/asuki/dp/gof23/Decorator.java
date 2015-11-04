package org.asuki.dp.gof23;

import static java.lang.System.out;
import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;

public class Decorator {

    /*
     * Normal version
     */

    interface Component {
        String operation(String input);
    }

    static class OriginalComponent implements Component {
        @Override
        public String operation(String input) {
            return input;
        }
    }

    static abstract class BaseDecorator implements Component {
        protected Component com;

        public BaseDecorator(Component com) {
            this.com = com;
        }
    }

    static class TrimDecorator extends BaseDecorator {

        public TrimDecorator(Component com) {
            super(com);
        }

        @Override
        public String operation(String input) {
            return this.com.operation(input).trim();
        }
    }

    static class CaseDecorator extends BaseDecorator {

        public CaseDecorator(Component com) {
            super(com);
        }

        @Override
        public String operation(String input) {
            return this.com.operation(input).toUpperCase();
        }
    }

    /*
     * Reflection version
     */

    interface Animal {
        void act();
    }

    static class Rat implements Animal {
        @Override
        public void act() {
            out.println(this.getClass().getSimpleName());
        }
    }

    interface Feature {
        void expand();
    }

    static class FlyFeature implements Feature {
        @Override
        public void expand() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class DigFeature implements Feature {
        @Override
        public void expand() {
            out.println(this.getClass().getSimpleName());
        }
    }

    @AllArgsConstructor
    static class AnimalDecorator implements Animal {
        private Animal animal;
        private Class<? extends Feature> feature;

        @Override
        public void act() {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {

                    Object obj = null;

                    if (isPublic(method.getModifiers())) {
                        obj = method.invoke(feature.newInstance(), args);
                    }

                    animal.act();

                    return obj;
                }
            };

            ClassLoader loader = getClass().getClassLoader();
            Feature proxy = (Feature) java.lang.reflect.Proxy.newProxyInstance(
                    loader, this.feature.getInterfaces(), handler);
            proxy.expand();
        }
    }
}
