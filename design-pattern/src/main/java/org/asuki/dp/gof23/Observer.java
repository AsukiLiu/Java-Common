package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Observer {

    /*
     * List version
     */

    interface Listener {
        void update();
    }

    static class ConcreteListener implements Listener {

        private final ConcreteSubject subject;
        private final String name;

        public ConcreteListener(ConcreteSubject subject, String name) {
            this.subject = subject;
            this.name = name;
        }

        @Override
        public void update() {
            out.println(this.name + ":" + this.subject.getState());
        }
    }

    static abstract class Subject {
        private List<Listener> listeners = new ArrayList<>();

        public void attatch(Listener listener) {
            this.listeners.add(listener);
        }

        public void detatch(Listener listener) {
            this.listeners.remove(listener);
        }

        protected void notifyListeners() {
            for (Listener listener : this.listeners) {
                listener.update();
            }
        }
    }

    static class ConcreteSubject extends Subject {
        @Getter
        private State state;

        public void setState(State state) {
            this.state = state;
            super.notifyListeners();
        }

    }

    /*
     * Map version
     */

    @AllArgsConstructor
    static class Event {
        @Getter
        private final State state;
    }

    interface OnListener {
        void handleMessage(Event event);
    }

    interface EventDispatcher {
        void addEventListener(State state, OnListener listener);

        void removeEventListener(State state);

        void dispatchEvent(Event event);
    }

    static class EventDispatcherImpl implements EventDispatcher {
        private static Map<State, OnListener> map = new EnumMap<>(State.class);

        @Override
        public void addEventListener(State state, OnListener listener) {
            map.put(state, listener);
        }

        @Override
        public void removeEventListener(State state) {
            map.remove(state);
        }

        @Override
        public void dispatchEvent(Event event) {

            if (map.containsKey(event.getState())) {
                map.get(event.getState()).handleMessage(event);
            }
        }
    }

    static class Register extends EventDispatcherImpl implements OnListener {

        public Register() {
            this.addEventListener(State.START, this);
            this.addEventListener(State.END, this);
        }

        @Override
        public void handleMessage(Event event) {
            out.println(event.getState());
        }

    }

    /*
     * JDK(Observer & Observable) version
     */

    static class ObserverA implements java.util.Observer {
        @Override
        public void update(java.util.Observable o, Object arg) {
            out.println(this.getClass().getSimpleName() + ":" + arg);
        }
    }

    static class ObserverB implements java.util.Observer {
        @Override
        public void update(java.util.Observable o, Object arg) {
            out.println(this.getClass().getSimpleName() + ":" + arg);
        }
    }

    static class ConcreteObservable extends java.util.Observable {
        public void inform() {
            super.setChanged();
            super.notifyObservers("info");
            super.clearChanged();
        }
    }

    /*
     * JDK(EventObject & EventListener) version
     */

    static class TargetEvent extends EventObject {

        private static final long serialVersionUID = 1L;

        @Getter
        private final State state;

        public TargetEvent(Object source, State state) {
            super(source);
            this.state = state;
        }
    }

    interface TargetListener extends EventListener {
        void update(TargetEvent event);
    }

    static class ConcreteListenerA implements TargetListener {
        @Override
        public void update(TargetEvent event) {
            out.println(this.getClass().getSimpleName() + ":"
                    + event.getState());
        }
    }

    static class ConcreteListenerB implements TargetListener {
        @Override
        public void update(TargetEvent event) {
            out.println(this.getClass().getSimpleName() + ":"
                    + event.getState());
        }
    }

    static class Manager {
        private Collection<TargetListener> listeners;

        public void addListener(TargetListener listener) {
            if (this.listeners == null) {
                this.listeners = new HashSet<TargetListener>();
            }

            this.listeners.add(listener);
        }

        public void removeListener(TargetListener listener) {
            if (this.listeners == null) {
                return;
            }

            this.listeners.remove(listener);
        }

        public void startEvent() {
            if (this.listeners == null) {
                return;
            }

            this.notifyListeners(new TargetEvent(this, State.START));
        }

        public void endEvent() {
            if (this.listeners == null) {
                return;
            }

            this.notifyListeners(new TargetEvent(this, State.END));
        }

        private void notifyListeners(TargetEvent event) {
            for (TargetListener listener : this.listeners) {
                listener.update(event);
            }
        }
    }

    /*
     * Generic version
     */

    static abstract class GenericObservable<O extends GenericObserver<A>, A> {

        protected List<O> observers;

        public GenericObservable() {
            this.observers = new CopyOnWriteArrayList<>();
        }

        public void addObserver(O observer) {
            this.observers.add(observer);
        }

        public void removeObserver(O observer) {
            this.observers.remove(observer);
        }

        public void notifyObservers(A argument) {
            for (O observer : observers) {
                observer.update(argument);
            }
        }
    }

    static class GenericManager extends GenericObservable<StateObserver, State> {

        public void startEvent() {
            super.notifyObservers(State.START);
        }

        public void endEvent() {
            super.notifyObservers(State.END);
        }
    }

    interface GenericObserver<A> {

        void update(A argument);
    }

    interface StateObserver extends GenericObserver<State> {
    }

    static class StateObserverA implements StateObserver {

        @Override
        public void update(State state) {
            out.println(this.getClass().getSimpleName() + ":" + state);
        }

    }

    static class StateObserverB implements StateObserver {

        @Override
        public void update(State state) {
            out.println(this.getClass().getSimpleName() + ":" + state);
        }

    }

    enum State {
        START, END;
    }

}
