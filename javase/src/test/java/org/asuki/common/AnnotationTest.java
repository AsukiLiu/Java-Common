package org.asuki.common;

import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;

import org.asuki.common.annotation.Hint;
import org.asuki.common.annotation.Hints;
import org.asuki.common.model.Person;
import org.asuki.common.model.Sub;
import org.asuki.common.util.AnnotationResolver;
import org.testng.annotations.Test;

public class AnnotationTest {

    @Test
    public void testAnnotation() {
        AnnotationResolver.resolveMethods(Person.class);
        AnnotationResolver.resolveSampledFields(Person.class);
    }

    @Test
    public void testRepeatable() {
        Hint hint = Person.class.getAnnotation(Hint.class);
        assertThat(hint, is(nullValue()));

        Hints hints = Person.class.getAnnotation(Hints.class);
        assertThat(hints.value().length, is(2));

        Hint[] hintHint = Person.class.getAnnotationsByType(Hint.class);
        assertThat(hintHint.length, is(2));
    }

    @Test
    public void testInherited() {
        Class<Sub> clazz = Sub.class;

        out.println("Field>>>");
        // public(Super + Sub)
        out.println(Arrays.toString(clazz.getFields()));
        // all(Sub)
        out.println(Arrays.toString(clazz.getDeclaredFields()));

        out.println("Method>>>");
        // public(Super + Sub)
        out.println(Arrays.toString(clazz.getMethods()));
        // all(Sub)
        out.println(Arrays.toString(clazz.getDeclaredMethods()));

        out.println("Constructor>>>");
        // public(Sub)
        out.println(Arrays.toString(clazz.getConstructors()));
        // all(Sub)
        out.println(Arrays.toString(clazz.getDeclaredConstructors()));

        out.println("Annotation>>>");
        assertThat(clazz.isAnnotationPresent(Hints.class), is(true));
        out.println(clazz.getAnnotation(Hints.class));
        // Super + Sub
        out.println(Arrays.toString(clazz.getAnnotations()));
        // Sub
        out.println(Arrays.toString(clazz.getDeclaredAnnotations()));
    }

}
