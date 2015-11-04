package org.asuki.dp.other;

import org.asuki.dp.other.Archetype.EventRecorderDecorator;
import org.asuki.dp.other.Archetype.EventRecorderDelegate;
import org.asuki.dp.other.Archetype.RecordEventToDatabase;
import org.asuki.dp.other.Archetype.ComplicateEventRecorder;
import org.testng.annotations.Test;

public class ArchetypeTest {

    @Test
    public void test() {
        EventRecorderDelegate delegate = new RecordEventToDatabase();
        EventRecorderDecorator decorator = new ComplicateEventRecorder();

        decorator.setDelegate(delegate);
        decorator.record("event");
    }
}
