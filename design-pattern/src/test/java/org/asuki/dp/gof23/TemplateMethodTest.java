package org.asuki.dp.gof23;

import static java.lang.System.out;

import org.asuki.dp.gof23.TemplateMethod.CallbackTemplate;
import org.asuki.dp.gof23.TemplateMethod.ConcreteReflectionTemplate;
import org.asuki.dp.gof23.TemplateMethod.ConcreteTemplate;
import org.asuki.dp.gof23.TemplateMethod.FileProcessable;
import org.asuki.dp.gof23.TemplateMethod.FileProcessors;
import org.asuki.dp.gof23.TemplateMethod.ReflectionTemplate;
import org.asuki.dp.gof23.TemplateMethod.SimpleFileProcessor;
import org.asuki.dp.gof23.TemplateMethod.SimpleLineProcessor;
import org.asuki.dp.gof23.TemplateMethod.Template;
import org.testng.annotations.Test;

public class TemplateMethodTest {

    @Test
    public void testNormalVersion() {
        Template template = new ConcreteTemplate();
        template.init();
    }

    @Test
    public void testCallbackVersion() {
        CallbackTemplate template = new CallbackTemplate();
        template.init(new ConcreteTemplate());
    }

    @Test
    public void testReflectionVersion() throws Exception {
        ReflectionTemplate template = new ConcreteReflectionTemplate();
        template.init();
    }

    @Test
    public void testFileProcessor() {
        final String FILE_NAME = "sample.txt";

        new SimpleFileProcessor().readFile(FILE_NAME);

        FileProcessors.readFile(FILE_NAME, new SimpleLineProcessor());

        FileProcessors.readFile(FILE_NAME, line -> out.println(line));

        FileProcessable processor = out::println;
        processor.readFile(FILE_NAME);
    }
}
