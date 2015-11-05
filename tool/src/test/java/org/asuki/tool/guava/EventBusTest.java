package org.asuki.tool.guava;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import lombok.Getter;

import org.testng.annotations.Test;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusTest {

    @Test
    public void testEvent() {

        EventBus eventBus = new EventBus("test");

        EventListener listener = new EventListener();
        eventBus.register(listener);

        eventBus.post(new TestEvent(100));
        eventBus.post(new TestEvent(200));
        eventBus.post(new TestEvent(300));

        assertThat(listener.getLastMessage(), is(300));

        eventBus.unregister(listener);
        eventBus.post(new TestEvent(400));

        assertThat(listener.getLastMessage(), is(300));
    }

    @Test
    public void testDeadEvent() {

        EventBus eventBus = new EventBus("test");

        DeadEventListener deadEventListener = new DeadEventListener();
        eventBus.register(deadEventListener);

        eventBus.post(new TestEvent(100));

        assertThat(deadEventListener.isNotDelivered(), is(true));
    }

    @Test
    public void testMultiEventAndSubEvent() {

        EventBus eventBus = new EventBus("test");

        MultipleListener multipleListener = new MultipleListener();
        NumberListener numberListener = new NumberListener();
        eventBus.register(multipleListener);
        eventBus.register(numberListener);

        eventBus.post(new Integer(100));

        assertThat(multipleListener.getLastInteger(), is(100));
        assertThat(multipleListener.getLastLong(), is(nullValue()));
        assertThat((Integer) numberListener.getLastNumber(), is(100));

        eventBus.post(new Long(200L));

        assertThat(multipleListener.getLastInteger(), is(100));
        assertThat(multipleListener.getLastLong(), is(200L));
        assertThat((Long) numberListener.getLastNumber(), is(200L));
    }

    private static class TestEvent {
        @Getter
        private final int message;

        public TestEvent(int message) {
            this.message = message;
        }
    }

    private static class EventListener {
        @Getter
        private int lastMessage;

        @Subscribe
        public void listen(TestEvent event) {
            lastMessage = event.getMessage();
        }
    }

    private static class NumberListener {
        @Getter
        private Number lastNumber;

        @Subscribe
        public void listen(Number number) {
            lastNumber = number;
        }
    }

    private static class MultipleListener {
        @Getter
        private Integer lastInteger;
        @Getter
        private Long lastLong;

        @Subscribe
        public void listen(Integer integer) {
            lastInteger = integer;
        }

        @Subscribe
        public void listen(Long mLong) {
            lastLong = mLong;
        }
    }

    private static class DeadEventListener {
        @Getter
        private boolean notDelivered = false;

        @Subscribe
        public void listen(DeadEvent event) {
            notDelivered = true;
        }
    }
}
