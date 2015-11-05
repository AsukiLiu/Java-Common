package org.asuki.common;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.SneakyThrows;

import org.asuki.common.model.Person;
import org.asuki.common.model.Student;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BasicTest {

    @Test
    public void testCast() {
        Object obj = 3;

        Integer objAsInt1 = staticCast1(obj);
        Integer objAsInt2 = staticCast2(obj);
        Integer objAsInt3 = dynamicCast(Integer.class, obj);
        Integer objAsInt4 = castInOptional(obj);

        assertThat(objAsInt1, is(objAsInt2));
        assertThat(objAsInt1, is(objAsInt3));
        assertThat(objAsInt1, is(objAsInt4));
    }

    private Integer staticCast1(Object obj) {
        return obj instanceof Integer ? (Integer) obj : null;
    }

    private Integer staticCast2(Object obj) {
        return Integer.class.isInstance(obj) ? Integer.class.cast(obj) : null;
    }

    private <T> T dynamicCast(Class<T> type, Object obj) {
        return type.isInstance(obj) ? type.cast(obj) : null;
    }

    private Integer castInOptional(Object obj) {
        Optional<?> objOpt = Optional.of(obj);
        Optional<Integer> objAsIntOpt = objOpt
                .filter(Integer.class::isInstance).map(Integer.class::cast);

        return objAsIntOpt.isPresent() ? objAsIntOpt.get() : null;
    }

    @Test
    public void testJoinString() {

        final String expected = "aaa,bbb";
        final String separator = ",";

        String csv = "";

        StringJoiner joiner = new StringJoiner(separator);
        joiner.add("aaa").add("bbb");

        csv = joiner.toString();
        assertThat(csv, is(expected));

        csv = String.join(separator, "aaa", "bbb");
        assertThat(csv, is(expected));

        List<String> strings = Arrays.asList("aaa", "bbb");

        csv = strings.stream().reduce((t, u) -> t + separator + u).get();
        assertThat(csv, is(expected));

        joiner = new StringJoiner(":", "[", "]");
        joiner.add("aaa").add("bbb");

        assertThat(joiner.toString(), is("[aaa:bbb]"));
    }

    @SneakyThrows
    @Test
    public void testLambda() {

        Callable<Double> pi = () -> 3.14;
        assertThat(pi.call(), is(3.14));

        String[] words = { "a", "bbb", "cc" };
        Arrays.sort(words, (s1, s2) -> s1.length() - s2.length());

        assertThat(words, is(new String[] { "a", "cc", "bbb" }));
    }

    @Test
    public void testListExtension() {
        List<String> list = new ArrayList<>(Arrays.asList("abc", "bcd", "cde"));

        list.replaceAll(str -> str.toUpperCase());
        assertThat(list.toString(), is("[ABC, BCD, CDE]"));

        list.removeIf(str -> str.contains("B"));
        assertThat(list.toString(), is("[CDE]"));
    }

    @Test
    public void testMapExtensionCaseA() {
        Map<String, String> map = new HashMap<>();
        map.put("aaa", "AAA");
        map.put("bbb", "BBB");
        map.put("ccc", "CCC");

        map.remove("aaa", "AAA");
        map.remove("bbb", "*");
        map.remove("*", "CCC");
        assertThat(map.toString(), is("{ccc=CCC, bbb=BBB}"));

        map.put("ddd", "DDD");

        map.replace("bbb", "BBB", "REPLACE");
        map.replace("ccc", "*", "NO REPLACE");
        map.replace("*", "DDD", "NO REPLACE");
        assertThat(map.toString(), is("{ccc=CCC, bbb=REPLACE, ddd=DDD}"));

        map.replace("ddd", "REPLACE");
        map.replace("*", "NO REPLACE");
        assertThat(map.toString(), is("{ccc=CCC, bbb=REPLACE, ddd=REPLACE}"));
    }

    @Test
    public void testMapExtensionCaseB() {

        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.putIfAbsent(i, "val" + i);
        }

        map.forEach((key, value) -> out
                .printf("Key: %s Value: %s ", key, value));

        map.compute(6, (key, value) -> "<" + value + ">");
        map.compute(7, (key, value) -> null);
        map.compute(15, (key, value) -> "val15");

        assertThat(map.get(6), is("<val6>"));
        assertThat(map.get(7), is(nullValue()));
        assertThat(map.get(15), is("val15"));

        map.computeIfPresent(4, (key, value) -> null);
        map.computeIfPresent(3, (key, value) -> value + key);

        assertThat(map.containsKey(4), is(false));
        assertThat(map.get(3), is("val33"));

        map.computeIfAbsent(3, value -> "aaa");
        map.computeIfAbsent(12, value -> "aaa");

        assertThat(map.get(3), is("val33"));
        assertThat(map.get(12), is("aaa"));

        assertThat(map.getOrDefault(10, "not found"), is("not found"));

        map.merge(9, "aaa", (value, newValue) -> value.concat(newValue));
        assertThat(map.get(9), is("val9aaa"));
    }

    @Test
    public void testPredicate() {

        Predicate<String> predicate = (s) -> s.length() > 0;

        assertThat(predicate.test("foo"), is(true));
        assertThat(predicate.negate().test("foo"), is(false));

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        assertThat(nonNull.test(null), is(false));
        assertThat(isNull.test(null), is(true));

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        assertThat(isEmpty.test(""), is(true));
        assertThat(isNotEmpty.test(""), is(false));
    }

    @Test
    public void testFunction() {
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> toString = toInteger.andThen(String::valueOf);

        assertThat(toInteger.apply("123"), is(123));
        assertThat(toString.apply("123"), is("123"));

        Function<Integer, Integer> f1 = x -> 1 + x;
        Function<Integer, Integer> f2 = x -> 2 * x;

        // f2 -> f1
        assertThat(f1.compose(f2).apply(0), is(1));
        // f1 -> f2
        assertThat(f1.andThen(f2).apply(0), is(2));
        // f1 -> f1 -> f2
        assertThat(f1.andThen(f2).compose(f1).apply(0), is(4));
    }

    @Test
    public void testSupplier() {

        final String expected = "Person(name=Andy, age=0)";

        Supplier<Person> personSupplier = Person::new;
        Person person = personSupplier.get();
        person.setName("Andy");

        assertThat(person.toString(), is(expected));

        PersonFactory<Person> personFactory = Person::new;
        person = personFactory.create("Andy");

        assertThat(person.toString(), is(expected));
    }

    @Test
    public void testConsumer() {
        Consumer<Person> consumer = (p) -> p.setName("Mr. " + p.getName());
        Person person = new Person("Andy");

        consumer.accept(person);

        assertThat(person.toString(), is("Person(name=Mr. Andy, age=0)"));
    }

    @Test
    public void testComparator() {
        Person person1 = new Person("Tom");
        Person person2 = new Person("Mike");
        Person person3 = new Person("John");
        Person person4 = new Person("Tom");

        Comparator<Person> comparator = (p1, p2) -> p1.getName().compareTo(
                p2.getName());

        assertThat(comparator.compare(person1, person2) > 0, is(true));
        assertThat(comparator.reversed().compare(person1, person2) > 0,
                is(false));
        assertThat(comparator.reversed().compare(person1, person4) == 0,
                is(true));

        List<Person> persons = Arrays.asList(person1, person2, person3);
        Collections.sort(persons, comparator);

        persons.forEach((s) -> out.println(s));
        persons.forEach(out::println);
    }

    @Test
    public void testOptional() {
        Optional<String> optional = Optional.of("abc");

        assertThat(optional.isPresent(), is(true));
        assertThat(optional.get(), is("abc"));
        assertThat(optional.orElse("none"), is("abc"));
        assertThat(optional.map(s -> s.toUpperCase()).orElse("none"), is("ABC"));
        assertThat(optional.flatMap(s -> Optional.of(s.toUpperCase())).orElse("none"), is("ABC"));
        assertThat(optional.filter(s -> s.length() > 5).orElse("none"), is("none"));

        optional.ifPresent(s -> out.println(s));

        optional = Optional.ofNullable(null);

        assertThat(optional.isPresent(), is(false));
        assertThat(optional.orElseGet(() -> "none"), is("none"));

        try {
            optional.orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            out.println(e);
        }
    }

    @Test
    public void testCustomFunction() {
        // (from) -> Integer.valueOf(from)
        Converter<String, Integer> valueOf = Integer::valueOf;

        assertThat(valueOf.convert("123"), is(123));

        MyClass instance = new MyClass();
        Converter<String, String> startsWith = instance::startsWith;

        assertThat(startsWith.convert("Java"), is("J"));

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Tom", 20));
        persons.add(new Person("Jack", 30));
        ListConverterImpl converter = new ListConverterImpl();

        assertThat(converter.convert(persons).toString(),
                is("[Student{name=Tom, age=20}, Student{name=Jack, age=30}]"));
    }

    @Test(dataProvider = "personData")
    public void testPersonValidator(Person person, boolean result) {
        PersonValidator validator = new PersonValidator();
        assertThat(validator.test(person), is(result));
    }

    @DataProvider
    private Object[][] personData() {
        // @formatter:off
        return new Object[][] { 
            { new Person("Tom", 30), true },
            { new Person(null, 30), false }, 
            { new Person("", 30), false },
            { new Person("Tom", 0), false } 
        };
        // @formatter:on
    }

}

@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}

class MyClass {
    public String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}

interface ListConverter<A, B> extends Function<A, B> {
    default List<B> convert(List<A> input) {
        return input.stream().map(this::apply).collect(toList());
    }
}

class ListConverterImpl implements ListConverter<Person, Student> {
    @Override
    public Student apply(Person person) {
        return new Student(person);
    }
}
