package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

public class Composite {

    /*
     * Method version
     */

    static abstract class Component {
        protected String name;

        public Component(String name) {
            this.name = name;
        }

        public void add(Component c) {
        };

        public void remove(Component c) {
        };

        public abstract void display();
    }

    static class Branch extends Component {
        private List<Component> children = new ArrayList<>();

        public Branch(String name) {
            super(name);
        }

        @Override
        public void add(Component c) {
            children.add(c);
        }

        @Override
        public void remove(Component c) {
            children.remove(c);
        }

        @Override
        public void display() {
            out.println(this.name);
            children.forEach(c -> c.display());
        }
    }

    static class Leaf extends Component {
        public Leaf(String name) {
            super(name);
        }

        @Override
        public void display() {
            out.println(this.name);
        }
    }

    /*
     * Constructor version
     */

    static abstract class Element {

        private List<Element> children = new ArrayList<>();

        public void add(Element e) {
            children.add(e);
        }

        protected void printBefore() {
        };

        protected void printAfter() {
        };

        public void print() {
            printBefore();
            children.forEach(c -> c.print());
            printAfter();
        }
    }

    static class Letter extends Element {

        private char c;

        public Letter(char c) {
            this.c = c;
        }

        @Override
        protected void printBefore() {
            out.print(c);
        }

    }

    static class Word extends Element {

        public Word(List<Letter> letters) {
            letters.forEach(l -> this.add(l));
        }

        @Override
        protected void printBefore() {
            out.print(" ");
        }

    }

    static class Sentence extends Element {

        public Sentence(List<Word> words) {
            words.forEach(w -> this.add(w));
        }

        @Override
        protected void printAfter() {
            out.println(".");
        }

    }

}
