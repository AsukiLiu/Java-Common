package org.asuki.common.util;

import static java.lang.System.out;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public final class AnnotationResolver {

    private AnnotationResolver() {
    }

    public static void resolveMethods(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            if (annotations.length == 0) {
                continue;
            }

            out.println(method.getName());
            Stream.of(annotations).forEach(out::println);
        }
    }

    public static void resolveSampledFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            out.println("Field: " + field.getName());

            Sample sampleAnnotation = field.getAnnotation(Sample.class);
            if (sampleAnnotation == null) {
                continue;
            }

            out.println("Value: " + sampleAnnotation.value());
            out.println("Items: " + String.join(",", sampleAnnotation.item()));
        }
    }
}
