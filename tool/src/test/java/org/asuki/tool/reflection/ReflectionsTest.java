package org.asuki.tool.reflection;

import static com.google.common.collect.Iterables.transform;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.Matchers.hasItems;
import static org.reflections.ReflectionUtils.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.testng.annotations.Test;

import com.google.common.base.Function;

public class ReflectionsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {

        Set<Method> getters = getAllMethods(Person.class, withModifier(PUBLIC),
                withPrefix("get"), withParametersCount(0));

        assertThat(getMethodNames(getters),
                hasItems("getLocation", "getName", "getId"));

        Set<Method> listMethods = getAllMethods(List.class,
                withParametersAssignableTo(Collection.class),
                withReturnType(boolean.class));

        assertThat(
                getMethodNames(listMethods),
                hasItems("removeAll", "addAll", "containsAll", "retainAll", "removeAll", "addAll", "containsAll", "retainAll"));

        Set<Field> fields = getAllFields(Person.class,
                withAnnotation(Option.class),
                withTypeAssignableTo(String.class));

        assertThat(getFieldNames(fields),
                hasItems("name", "location"));
    }

    private Iterable<String> getMethodNames(Set<Method> getters) {
        return transform(getters, new Function<Method, String>() {
            public String apply(Method method) {
                return method.getName();
            };
        });
    }

    private Iterable<String> getFieldNames(Set<Field> getters) {
        return transform(getters, new Function<Field, String>() {
            public String apply(Field field) {
                return field.getName();
            };
        });
    }

    @Getter
    @Setter
    private static class Person {
        private String id;

        @Option
        private String name;

        @Option
        private String location;
    }

    @Target({ FIELD, TYPE })
    @Retention(RUNTIME)
    private @interface Option {
    }
}
