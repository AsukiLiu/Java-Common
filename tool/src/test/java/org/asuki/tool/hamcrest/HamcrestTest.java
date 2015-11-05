package org.asuki.tool.hamcrest;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.asuki.tool.hamcrest.IsNotANumber.notANumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hamcrest.Matcher;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class HamcrestTest {

    @Test
    public void testString() {
        String str1 = "foo";
        String str2 = "foobarbaz";
        String str3 = "foo bar baz";
        Class<?> clazz = String.class;

        assertThat(null, is(nullValue()));
        assertThat(str1, is(notNullValue()));
        assertThat("", isEmptyString());
        assertThat("", isEmptyOrNullString());
        assertThat(null, isEmptyOrNullString());

        assertThat(str1, is("foo"));
        assertThat(str1, is(not(str2)));
        assertThat(str1, is(not(equalTo(str2))));

        assertThat(str1, is(instanceOf(String.class)));
        assertThat(clazz, is(typeCompatibleWith(CharSequence.class)));
        assertThat(clazz, is(not(typeCompatibleWith(Number.class))));

        Hoge one = new Hoge("name");
        Hoge another = new Hoge("name");
        assertThat(one, is(equalTo(another))); // equals()
        assertThat(one, is(not(sameInstance(another)))); // ==

        one.setAge(20);
        another.setAge(20);
        assertThat(one, is(comparesEqualTo(another)));  // compareTo()

        assertThat(str1, is(equalToIgnoringCase("FOO")));
        assertThat(str3,
                is(equalToIgnoringWhiteSpace("foo        bar        baz")));
        assertThat(str2, startsWith("foo"));
        assertThat(str2, endsWith("baz"));
        assertThat(str2, containsString("bar"));
        assertThat("AaaBbbCcc",
                is(stringContainsInOrder(asList("Aaa", "aaBbb", "bCcc"))));
    }

    @Test
    public void testNumber() {
        double num = 1.0;

        assertThat(num, is(greaterThan(0.5)));
        assertThat(num, is(greaterThanOrEqualTo(1.0)));
        assertThat(num, is(lessThan(1.1)));
        assertThat(num, is(lessThanOrEqualTo(1.0)));
        assertThat(num, is(closeTo(0.95, 0.1))); // 1.0 = 0.95±0.1
        assertThat(num, is(not(closeTo(0.95, 0.01)))); // 1.0 != 0.95±0.01
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testArray() {
        assertThat(new String[] {}, is(emptyArray()));

        String[] array = { "foo", "bar", "baz" };
        assertThat(array,
                is(array(equalTo("foo"), startsWith("b"), endsWith("z"))));
        assertThat(array, is(arrayContaining("foo", "bar", "baz")));
        assertThat(array, is(arrayContainingInAnyOrder("foo", "baz", "bar")));
        assertThat(array, is(arrayWithSize(3)));
        assertThat(array, hasItemInArray("bar"));
        assertThat("bar", isIn(array));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCollection() {
        String str = "foo";
        String[] array = { "foo", "foobar", "foobarbaz" };
        Collection<String> collet = asList("foo", "bar", "baz");
        Map<String, String> map = new HashMap<>();

        assertThat(map, equalTo(Collections.EMPTY_MAP));

        map.put("A", "a");
        map.put("B", "b");

        assertThat(collet, hasItem("bar"));
        assertThat(collet, hasItems("bar", "baz"));
        assertThat(str, isIn(collet));
        assertThat(str, isOneOf(array));

        assertThat(map, hasEntry("A", "a"));
        assertThat(map, not(hasEntry("Z", "z")));

        assertThat(map, hasKey("A"));
        assertThat(map, not(hasKey("Z")));

        assertThat(map, hasValue("a"));
        assertThat(map, not(hasValue("z")));

        assertThat(
                collet,
                is(contains(equalTo("foo"), containsString("a"),
                        startsWith("b"))));
        assertThat(collet, is(containsInAnyOrder("foo", "baz", "bar")));
        assertThat(collet, hasSize(3));

        List<String> list = Collections.emptyList();
        assertThat(list, is(empty()));
        assertThat(list, is(emptyCollectionOf(String.class)));

        List<Integer> numbers = newArrayList(15, 20, 25, 30);
        assertThat(numbers, everyItem(greaterThan(10)));

        Iterable<String> iterable = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return Collections.emptyIterator();
            }
        };
        assertThat(iterable, is(emptyIterable()));
        assertThat(iterable, is(emptyIterableOf(String.class)));

        iterable = newArrayList("ab", "cd", "ef");
        assertThat(iterable, Matchers.<String>iterableWithSize(3));
    }

    @Test
    public void testEvent() {
        Object obj1 = new Object();
        Object obj2 = new Object();

        EventObject event = new EventObject(obj1);

        assertThat(event, is(eventFrom(obj1)));
        assertThat(event, is(not(eventFrom(obj2))));
    }

    @Test
    public void testBean() {
        Hoge one = new Hoge();
        one.setName("Tom");

        assertThat(one, hasProperty("name"));
        assertThat(one, hasProperty("name", is("Tom")));
        assertThat(one, not(hasProperty("address")));

        assertThat(one, hasToString(equalTo("Hoge[Tom]")));
        assertThat(one.toString(), is(equalTo("Hoge[Tom]")));

        Hoge another = new Hoge("Tom");
        assertThat(one, is(samePropertyValuesAs(another)));
    }

    @Test
    public void testLogical() {
        String str1 = "foobarbaz";
        String str2 = "foo bar baz";

        Collection<Matcher<? super String>> matchers = new ArrayList<>();
        matchers.add(containsString("foo"));
        matchers.add(containsString("oba"));
        matchers.add(endsWith("az"));

        assertThat(str1, is(allOf(matchers)));
        assertThat(str2, is(anyOf(matchers)));
    }

    @Test
    public void testCustom() {
        assertThat(Math.sqrt(-1), is(notANumber()));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    public class Hoge implements Comparable<Hoge> {

        private String name;
        private int age;

        public Hoge(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Hoge[" + name + "]";
        }

        @Override
        public int compareTo(Hoge hoge) {
            return this.age - hoge.age;
        }
    }
}
