package org.asuki.dp.other;

import static java.lang.System.out;
import lombok.Setter;

public class Archetype {

    interface EventRecorder {
        void record(String event);
    }

    /*
     * Concrete implements
     */

    static abstract class EventRecorderDelegate implements EventRecorder {
    }

    static class RecordEventToDatabase extends EventRecorderDelegate {
        @Override
        public void record(String event) {
            out.println("Record by database");
        }
    }

    static class RecordEventToFile extends EventRecorderDelegate {
        @Override
        public void record(String event) {
            out.println("Record by file");
        }
    }

    /*
     * Business logic
     */

    static abstract class EventRecorderDecorator implements EventRecorder {
        @Setter
        protected EventRecorderDelegate delegate;
    }

    static class SimpleEventRecorder extends EventRecorderDecorator {
        @Override
        public void record(String event) {
            out.println("Simply record");

            delegate.record(event);
        }
    }

    static class ComplicateEventRecorder extends EventRecorderDecorator {
        @Override
        public void record(String event) {
            out.println("Complicatedly record");

            delegate.record(event);
        }
    }
}
