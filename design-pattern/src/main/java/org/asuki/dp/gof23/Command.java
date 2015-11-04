package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.Deque;
import java.util.LinkedList;

import lombok.AllArgsConstructor;

public class Command {

    // Receiver
    interface Editor {
        void bold(boolean onOff);

        void italic(boolean onOff);
    }

    static class Document implements Editor {

        private boolean isBold;
        private boolean isItalic;

        @Override
        public void bold(boolean onOff) {
            this.isBold = onOff;
        }

        @Override
        public void italic(boolean onOff) {
            this.isItalic = onOff;
        }

        public void printStatus() {
            StringBuilder sb = new StringBuilder("Font:");
            if (this.isBold) {
                sb.append(" bold ");
            }
            if (this.isItalic) {
                sb.append(" italic ");
            }

            out.println(sb.toString());
        }

    }

    // Command
    @AllArgsConstructor
    static abstract class Font {
        protected final Editor editor;

        abstract public void execute();

        abstract public void undo();

        abstract public void redo();
    }

    static class Bold extends Font {

        public Bold(Editor editor) {
            super(editor);
        }

        @Override
        public void execute() {
            editor.bold(true);
        }

        @Override
        public void undo() {
            editor.bold(false);
        }

        @Override
        public void redo() {
            execute();
        }
    }

    static class Italic extends Font {

        public Italic(Editor editor) {
            super(editor);
        }

        @Override
        public void execute() {
            editor.italic(true);
        }

        @Override
        public void undo() {
            editor.italic(false);
        }

        @Override
        public void redo() {
            execute();
        }
    }

    static class Invoker {
        private Deque<Font> undoDeque = new LinkedList<>();
        private Deque<Font> redoDeque = new LinkedList<>();

        public void execute(Font font) {
            font.execute();
            undoDeque.offerLast(font);
        }

        public void undoLast() {
            if (undoDeque.isEmpty()) {
                return;
            }

            Font previousFont = undoDeque.pollLast();
            redoDeque.offerLast(previousFont);
            previousFont.undo();
        }

        public void redoLast() {
            if (redoDeque.isEmpty()) {
                return;
            }

            Font previousFont = redoDeque.pollLast();
            undoDeque.offerLast(previousFont);
            previousFont.redo();
        }

    }

}
