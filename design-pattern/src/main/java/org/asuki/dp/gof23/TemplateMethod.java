package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TemplateMethod {

    static class Manager {

        private Template template;

        public Manager(Template template) {
            this.template = template;
        }

        public void init() {
            template.init();
        }

        public void changeTemplate(Template template) {
            this.template = template;
        }
    }

    /*
     * Normal version
     */

    static abstract class Template {
        public final void init() {
            preProcess();

            initUser();
            initPasswd();
            initOther();

            postProcess();
        }

        abstract void initUser();

        abstract void initPasswd();

        abstract void initOther();

        private void preProcess() {
        }

        private void postProcess() {
        }
    }

    static class ConcreteTemplate extends Template implements ICallback {
        @Override
        public void initUser() {
            out.println("initUser");
        }

        @Override
        public void initPasswd() {
            out.println("initPasswd");
        }

        @Override
        public void initOther() {
            out.println("initOther");
        }
    }

    /*
     * Callback version
     */

    static class CallbackTemplate {
        public final void init(ICallback callback) {
            preProcess();

            callback.initUser();
            callback.initPasswd();
            callback.initOther();

            postProcess();
        }

        private void preProcess() {
        }

        private void postProcess() {
        }
    }

    interface ICallback {
        void initUser();

        void initPasswd();

        void initOther();
    }

    /*
     * Reflection version
     */

    static abstract class ReflectionTemplate {
        public final void init() throws Exception {
            Method[] methods = getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (this.isInitMethod(method)) {
                    method.invoke(this);
                }
            }
        }

        private boolean isInitMethod(Method method) {
            // @formatter:off
            return method.getName().startsWith("init") &&          // initXXX()
                    Modifier.isPublic(method.getModifiers()) &&     // public
                    method.getReturnType().equals(Void.TYPE) &&    // void
                    !method.isVarArgs() &&                          // no arguments
                    !Modifier.isAbstract(method.getModifiers());    // not abstract method
            // @formatter:on
        }
    }

    static class ConcreteReflectionTemplate extends ReflectionTemplate {
        public void initUser() {
            out.println("initUser");
        }

        public void initPasswd() {
            out.println("initPasswd");
        }

        public void initOther() {
            out.println("initOther");
        }
    }

    /*
     * Template method version
     */

    static abstract class FileProcessor {
        public void readFile(String fileName) {
            String line = "dummy line";
            doProcessLine(line);
        }

        protected abstract void doProcessLine(String line);
    }

    static class SimpleFileProcessor extends FileProcessor {
        @Override
        protected void doProcessLine(String line) {
            out.println(line);
        }
    }

    /*
     * Strategy version
     */

    interface LineProcessor {
        void doProcessLine(String line);
    }

    static class SimpleLineProcessor implements LineProcessor {
        @Override
        public void doProcessLine(String line) {
            out.println(line);
        }
    }

    static final class FileProcessors {
        public static void readFile(String fileName, LineProcessor processor) {
            String line = "dummy line";
            processor.doProcessLine(line);
        }
    }

    /*
     * Lamda(Java 8) version
     */

    @FunctionalInterface
    interface FileProcessable {
        public default void readFile(String fileName) {
            String line = "dummy line";
            doProcessLine(line);
        }

        void doProcessLine(String line);
    }

}
