package org.asuki.dp.gof23;

import static org.asuki.dp.gof23.Observer.State.END;
import static org.asuki.dp.gof23.Observer.State.START;

import org.asuki.dp.gof23.Observer.ConcreteListener;
import org.asuki.dp.gof23.Observer.ConcreteListenerA;
import org.asuki.dp.gof23.Observer.ConcreteListenerB;
import org.asuki.dp.gof23.Observer.ConcreteObservable;
import org.asuki.dp.gof23.Observer.ConcreteSubject;
import org.asuki.dp.gof23.Observer.Event;
import org.asuki.dp.gof23.Observer.GenericManager;
import org.asuki.dp.gof23.Observer.Manager;
import org.asuki.dp.gof23.Observer.ObserverA;
import org.asuki.dp.gof23.Observer.ObserverB;
import org.asuki.dp.gof23.Observer.Register;
import org.asuki.dp.gof23.Observer.StateObserverA;
import org.asuki.dp.gof23.Observer.StateObserverB;
import org.testng.annotations.Test;

public class ObserverTest {

    @Test
    public void testListVersion() {
        ConcreteSubject subject = new ConcreteSubject();
        subject.attatch(new ConcreteListener(subject, "Observer1"));
        subject.attatch(new ConcreteListener(subject, "Observer2"));

        subject.setState(START);
        subject.setState(END);
    }

    @Test
    public void testMapVersion() {
        Register register = new Register();
        register.dispatchEvent(new Event(END));
        register.dispatchEvent(new Event(START));
    }

    @Test
    public void testJdkObserverVersion() {
        ConcreteObservable co = new ConcreteObservable();
        co.addObserver(new ObserverA());
        co.addObserver(new ObserverB());

        co.inform();
    }

    @Test
    public void testJdkEventVersion() {
        Manager manager = new Manager();
        manager.addListener(new ConcreteListenerA());
        manager.addListener(new ConcreteListenerB());

        manager.startEvent();
        manager.endEvent();
    }

    @Test
    public void testGenericVersion() {
        GenericManager manager = new GenericManager();
        manager.addObserver(new StateObserverA());
        manager.addObserver(new StateObserverB());

        manager.startEvent();
        manager.endEvent();
    }
}
