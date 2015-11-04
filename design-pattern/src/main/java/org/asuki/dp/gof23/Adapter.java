package org.asuki.dp.gof23;

import static java.lang.System.out;

public class Adapter {

    interface Adapterable {
        void request();
    }

    static class Adaptee {
        public void specificRequest() {
            out.println("specificRequest executed");
        }
    }

    /*
     * Object way
     */

    static class AdapterImplA implements Adapterable {
        private Adaptee adaptee = new Adaptee();

        @Override
        public void request() {
            adaptee.specificRequest();
        }
    }

    /*
     * Class way
     */

    static class AdapterImplB extends Adaptee implements Adapterable {
        @Override
        public void request() {
            specificRequest();
        }
    }
}
