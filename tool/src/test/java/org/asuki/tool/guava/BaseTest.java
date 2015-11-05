package org.asuki.tool.guava;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Strings.commonPrefix;
import static com.google.common.base.Strings.commonSuffix;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Strings.padStart;
import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.Iterables.toArray;
import static java.lang.System.out;
import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Defaults;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.Flushables;
import com.google.common.io.Resources;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.net.InetAddresses;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Primitives;

public class BaseTest {

    private Customer customer1 = new Customer(1, "Name1");
    private Customer customer2 = new Customer(2, "Name2");
    private Customer customer3 = new Customer(3, "Name3");

    /* Base */

    @Test
    public void testOptional() {

        Optional<Integer> possible = Optional.of(3);

        if (possible.isPresent()) {
            assertEquals(possible.get(), Integer.valueOf(3));
        }

        assertEquals(possible.or(10), Integer.valueOf(3));

        Long result = 0L;

        Optional<Long> nullValue = Optional.absent();
        // Optional<Long> nullValue = Optional.fromNullable(null);
        assertEquals(nullValue.orNull(), null);

        result = nullValue.isPresent() ? nullValue.get() : nullValue.or(-1L);
        assertEquals((long) result, -1L);

        Optional<Long> notNullValue = Optional.fromNullable(5L);
        assertEquals((long) notNullValue.orNull(), 5L);

        result = notNullValue.isPresent() ? notNullValue.get() : notNullValue
                .or(-1L);
        assertEquals((long) result, 5L);
        assertEquals(notNullValue.asSet().size(), 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testPreconditions() {

        List<Integer> list = Lists.newArrayList(1, 2, 3);
        checkElementIndex(2, list.size());
        checkPositionIndex(3, list.size());
        checkPositionIndexes(1, 3, list.size());

        checkNotNull(customer2.getId());
        checkNotNull(customer2.getId(), "%s is null", "ID");

        checkState(!customer2.isSick());

        checkArgument(customer2.getAddress() != null,
                "Not found the address of customer[id:%s]", customer2.getId());

        fail("No exception happened!");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testObjects() {

        Object[] customers = new Object[] { customer1, customer2 };

        assertEquals(Arrays.hashCode(customers),
                Objects.hashCode(customer1, customer2));

        assertFalse(Objects.equal("a", null));
        assertTrue(Objects.equal(null, null));

        Integer a = Objects.firstNonNull(null, 3);
        Integer b = Objects.firstNonNull(9, 3);

        assertEquals(a, Integer.valueOf(3));
        assertEquals(b, Integer.valueOf(9));

        Objects.firstNonNull(null, null);

        fail("No exception happened!");
    }

    @Test
    public void testStrings() {

        assertNull(emptyToNull(""));
        assertEquals(nullToEmpty(null), "");
        assertTrue(isNullOrEmpty(""));

        assertEquals(repeat("*", 10), "**********");
        assertEquals(padStart("7", 2, '0'), "07");
        assertEquals(padEnd("7", 2, '0'), "70");

        assertEquals(commonSuffix("careful","beautiful"), "ful");
        assertEquals(commonPrefix("regret","recollect"), "re");

        String str1 = LOWER_CAMEL.to(LOWER_UNDERSCORE, "someString");
        assertEquals(str1, "some_string");

        String str2 = LOWER_UNDERSCORE.to(LOWER_CAMEL, "some_string");
        assertEquals(str2, "someString");
    }

    @Test
    public void testJoinerAndSplitter() {

        ImmutableSet<String> strings = ImmutableSet.of("A", "B", "C");
        String joined = Joiner.on(":").join(strings);
        assertEquals(joined, "A:B:C");

        final String string = ": A::: B : C :::";

        // String[] parts = string.split(":");
        Iterable<String> parts = Splitter.on(":").omitEmptyStrings()
                .trimResults().split(string);

        joined = Joiner.on(":").join(parts);
        assertEquals(joined, "A:B:C");

        Iterable<String> splitByLimit = Splitter.on(":").limit(2).split(joined);
        assertEquals(splitByLimit.toString(), "[A, B:C]");

        Iterable<String> splitByFixedLength = Splitter.fixedLength(2).split(
                joined);
        assertEquals(splitByFixedLength.toString(), "[A:, B:, C]");

        String[] array = new String[] { "aa", "bb", null, "cc" };

        assertEquals(Joiner.on(",").skipNulls().join(array), "aa,bb,cc");
        assertEquals(Joiner.on(',').useForNull("-").join(array), "aa,bb,-,cc");

        Map<String, String> source = new TreeMap<>();
        source.put("xxx", "yyy");
        source.put("iii", "jjj");

        StringBuilder sb = new StringBuilder();
        Joiner.on(',').withKeyValueSeparator("=").appendTo(sb, source);
        assertEquals(sb.toString(), "iii=jjj,xxx=yyy");

        Joiner.on(',').skipNulls().appendTo(sb, "a", "b", "c");
        assertEquals(sb.toString(), "iii=jjj,xxx=yyya,b,c");

        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("key1", "value1");
        dictionary.put("key2", "value2");
        dictionary.put("key3", null);

        MapJoiner joiner = Joiner.on(", ").withKeyValueSeparator(":")
                .useForNull("none");

        assertEquals(joiner.join(dictionary),
                "key1:value1, key2:value2, key3:none");

        String str = "xxx=yyy#aaa=bbb";
        MapSplitter mapSplitter = Splitter.on("#").withKeyValueSeparator("=");
        Map<String, String> splitMap = mapSplitter.split(str);

        assertEquals(splitMap.toString(), "{xxx=yyy, aaa=bbb}");
    }

    @Test
    public void testCharMatcher() {

        Charset utf8 = Charsets.UTF_8;
        assertTrue(utf8.canEncode());

        byte[] bytes = "foo".getBytes(Charsets.UTF_8);
        String string = new String(bytes, StandardCharsets.UTF_8);
        assertEquals(string, "foo");

        CharMatcher matcher = CharMatcher.anyOf("andy");
        assertTrue(matcher.matches('a'));

        ImmutableList<Character> someChars = ImmutableList.of('a', 'b', 'c',
                'd', 'e');
        Collection<Character> filter = Collections2.filter(someChars, matcher);

        ImmutableList<Character> result = ImmutableList.of('a', 'd');

        assertEquals(toArray(filter, Character.class),
                toArray(result, Character.class));

        String telnum = "090 1234 5678";
        matcher = CharMatcher.WHITESPACE.or(CharMatcher.is('-'));

        Iterable<String> splits = Splitter.on(matcher).split(telnum);
        // JDK way
        String[] splitsByJdk = telnum.split("\\s|-");

        assertEquals(splits.toString(), "[090, 1234, 5678]");
        assertEquals(toArray(splits, String.class), splitsByJdk);

        String stringWithLinebreaks = "aaa\nbbb\nccc";
        assertEquals(CharMatcher.BREAKING_WHITESPACE.replaceFrom(
                stringWithLinebreaks, ' '), "aaa bbb ccc");

        String tabsAndSpaces = "   String with spaces and         tabs  ";

        assertEquals(CharMatcher.WHITESPACE.collapseFrom(tabsAndSpaces, ' '),
                " String with spaces and tabs ");
        assertEquals(
                CharMatcher.WHITESPACE.trimAndCollapseFrom(tabsAndSpaces, ' '),
                "String with spaces and tabs");

        String lettersAndNumbers = "foo123bar456";
        assertEquals(CharMatcher.JAVA_DIGIT.retainFrom(lettersAndNumbers),
                "123456");
    }

    @Test
    public void testPrimitives() {
        Set<Class<?>> primitiveClazzSet = Primitives.allPrimitiveTypes();
        out.println(primitiveClazzSet);

        Set<Class<?>> wrapperClazzSet = Primitives.allWrapperTypes();
        out.println(wrapperClazzSet);

        assertEquals(Primitives.isWrapperType(double.class), false);
        assertEquals(Primitives.isWrapperType(Double.class), true);
        assertEquals(Primitives.isWrapperType(String.class), false);

        assertEquals(Primitives.unwrap(Boolean.class), boolean.class);
        assertEquals(Primitives.wrap(boolean.class), Boolean.class);
    }

    @Test
    public void testInts() {
        Integer defaultValue = Defaults.defaultValue(int.class);
        assertEquals(defaultValue.intValue(), 0);

        int[] srcArray = { 1, 2, 3, 5 };

        assertEquals(Ints.max(srcArray), 5);
        assertEquals(Ints.min(srcArray), 1);
        assertEquals(Ints.join(" : ", srcArray), "1 : 2 : 3 : 5");

        assertEquals(Ints.contains(srcArray, 5), true);
        assertEquals(Ints.contains(srcArray, 0), false);

        assertEquals(Ints.compare(1, 2), -1);
        assertEquals(Ints.compare(2, 1), 1);
        assertEquals(Ints.compare(1, 1), 0);

        // List<int[]> destList = Arrays.asList(srcArray);
        List<Integer> destList = Ints.asList(srcArray);
        assertEquals(destList.toString(), "[1, 2, 3, 5]");

        // Integer[] destArray = (Integer[]) destList.toArray(new Integer[destList.size()]);
        int[] destArray = Ints.toArray(destList);
        assertEquals(Arrays.toString(destArray), "[1, 2, 3, 5]");

        int[] srcArray2 = { 8, 9 };
        int[] destArray2 = Ints.concat(srcArray, srcArray2);
        assertEquals(Arrays.toString(destArray2), "[1, 2, 3, 5, 8, 9]");
    }

    @Test
    public void testLongs() {
        Long defaultValue = Defaults.defaultValue(long.class);
        assertEquals(defaultValue.longValue(), 0L);

        long[] srcArray = { 1L, 2L, 3L, 5L };

        assertEquals(Longs.max(srcArray), 5L);
        assertEquals(Longs.min(srcArray), 1L);
        assertEquals(Longs.join(" : ", srcArray), "1 : 2 : 3 : 5");

        assertEquals(Longs.contains(srcArray, 5L), true);
        assertEquals(Longs.contains(srcArray, 0L), false);

        assertEquals(Longs.compare(1L, 2L), -1);
        assertEquals(Longs.compare(2L, 1L), 1);
        assertEquals(Longs.compare(1L, 1L), 0);

        // List<long[]> destList = Arrays.asList(srcArray);
        List<Long> destList = Longs.asList(srcArray);
        assertEquals(destList.toString(), "[1, 2, 3, 5]");

        // Long[] destArray = (Long[]) destList.toArray(new Long[destList.size()]);
        long[] destArray = Longs.toArray(destList);
        assertEquals(Arrays.toString(destArray), "[1, 2, 3, 5]");

        long[] srcArray2 = { 8L, 9L };
        long[] destArray2 = Longs.concat(srcArray, srcArray2);
        assertEquals(Arrays.toString(destArray2), "[1, 2, 3, 5, 8, 9]");
    }

    @Test
    public void testDoubles() {
        Double defaultValue = Defaults.defaultValue(double.class);
        assertEquals(defaultValue.doubleValue(), 0.0);

        double[] srcArray = { 1.1, 2.2, 3.3, 5.5 };

        assertEquals(Doubles.max(srcArray), 5.5);
        assertEquals(Doubles.min(srcArray), 1.1);
        assertEquals(Doubles.join(" : ", srcArray), "1.1 : 2.2 : 3.3 : 5.5");

        assertEquals(Doubles.contains(srcArray, 5.5), true);
        assertEquals(Doubles.contains(srcArray, 0.0), false);

        assertEquals(Doubles.compare(1.1, 2.2), -1);
        assertEquals(Doubles.compare(2.2, 1.1), 1);
        assertEquals(Doubles.compare(1.1, 1.1), 0);

        // List<double[]> destList = Arrays.asList(srcArray);
        List<Double> destList = Doubles.asList(srcArray);
        assertEquals(destList.toString(), "[1.1, 2.2, 3.3, 5.5]");

        // Double[] destArray = (Double[]) destList.toArray(new Double[destList.size()]);
        double[] destArray = Doubles.toArray(destList);
        assertEquals(Arrays.toString(destArray), "[1.1, 2.2, 3.3, 5.5]");

        double[] srcArray2 = { 8.8, 9.9 };
        double[] destArray2 = Doubles.concat(srcArray, srcArray2);
        assertEquals(Arrays.toString(destArray2),
                "[1.1, 2.2, 3.3, 5.5, 8.8, 9.9]");

        assertEquals(Doubles.isFinite(1.9999), true);
        assertEquals(Doubles.isFinite(Double.POSITIVE_INFINITY), false);
        assertEquals(Doubles.isFinite(Double.NEGATIVE_INFINITY), false);
        assertEquals(Doubles.isFinite(Double.NaN), false);
    }

    @Test
    public void testBooleans() {
        Boolean defaultValue = Defaults.defaultValue(boolean.class);
        assertEquals(defaultValue.booleanValue(), false);

        boolean[] srcArray = { true, false, true };

        assertEquals(Booleans.join(" : ", srcArray), "true : false : true");

        // List<boolean[]> destList = Arrays.asList(srcArray);
        List<Boolean> destList = Booleans.asList(srcArray);
        assertEquals(destList.toString(), "[true, false, true]");

        // Boolean[] destArray = (Boolean[]) destList.toArray(new Boolean[destList.size()]);
        boolean[] destArray = Booleans.toArray(destList);
        assertEquals(Arrays.toString(destArray), "[true, false, true]");

        boolean[] srcArray2 = { false, false };
        boolean[] destArray2 = Booleans.concat(srcArray, srcArray2);
        assertEquals(Arrays.toString(destArray2),
                "[true, false, true, false, false]");
    }

    @Test(expectedExceptions = ArithmeticException.class, dataProvider = "checkedData")
    public void testIntMathAndLongMath(Supplier<?> supplier) {
        supplier.get();

        fail("No exception happened!");
    }

    @DataProvider
    private Object[][] checkedData() {
        // @formatter:off

        Supplier<?> checkedAddInt = () -> IntMath.checkedAdd(Integer.MAX_VALUE, 1);
        Supplier<?> checkedSubtractInt = () -> IntMath.checkedSubtract(Integer.MIN_VALUE, 1);
        Supplier<?> checkedMultiplyInt = () -> IntMath.checkedMultiply(Integer.MAX_VALUE, 5);
        Supplier<?> checkedPowInt = () -> IntMath.checkedPow(Integer.MAX_VALUE, 2);

        Supplier<?> checkedAddLong = () -> LongMath.checkedAdd(Long.MAX_VALUE, 1L);
        Supplier<?> checkedSubtractLong = () -> LongMath.checkedSubtract(Long.MIN_VALUE, 1L);
        Supplier<?> checkedMultiplyLong = () -> LongMath.checkedMultiply(Long.MAX_VALUE, 5L);
        Supplier<?> checkedPowLong = () -> LongMath.checkedPow(Long.MAX_VALUE, 2);

        return new Object[][] { 
                { checkedAddInt }, { checkedSubtractInt }, { checkedMultiplyInt }, { checkedPowInt }, 
                { checkedAddLong }, { checkedSubtractLong }, { checkedMultiplyLong }, { checkedPowLong }, 
        };

        // @formatter:on
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testThrowables1() {
        try {
            throw new IOException();
        } catch (Throwable t) {
            // Throwable root = Throwables.getRootCause(t);
            // List<Throwable> exceptions = Throwables.getCausalChain(t);
            String statckTrace = Throwables.getStackTraceAsString(t);
            out.println(statckTrace);

            Throwables.propagate(t);
        }

        fail("No exception happened!");
    }

    @Test(expectedExceptions = IOException.class)
    public void testThrowables2() throws IOException {
        try {
            throw new IOException();
        } catch (Throwable t) {
            Throwables.propagateIfInstanceOf(t, IOException.class);
            // Throwables.propagateIfPossible(t, IOException.class);

            Throwables.propagate(t);
        }

        fail("No exception happened!");
    }

    /* IO */

    @Test
    public void testFiles() {

        final String fileName = "test.txt";
        final File file = new File(fileName);
        final String content = "This is test!";

        try {
            Files.touch(file);

            Files.write(content, file, UTF_8);

            Files.toByteArray(file);
            // Files.newInputStreamSupplier(file);

            assertEquals(Files.readFirstLine(file, UTF_8), content);
            assertEquals(Files.toString(file, UTF_8), content);

            java.nio.file.Files.deleteIfExists(Paths.get(fileName));

        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    @Test
    public void testIO() throws IOException {

        InputStream inputStream = System.in;

        // JDK way
        try {
            inputStream.close();
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        // Closeables.closeQuietly(inputStream);
        Closeables.close(inputStream, true);

        PrintStream outputStream = System.out;
        Flushables.flushQuietly(outputStream);
    }

    @Test
    public void testResources() {

        final String location = "org/asuki/tool/guava/BaseTest.class";

        URL guavaWay = Resources.getResource(location);
        checkArgument(guavaWay != null, "Resource[%s] not found", location);

        URL jdkWay = getClass().getClassLoader().getResource(location);
        checkArgument(jdkWay != null, "Resource[%s] not found", location);

        assertEquals(guavaWay, jdkWay);
    }

    /* Net */

    @Test
    public void testAddresses() {

        try {
            assertEquals(InetAddresses.forString("0.0.0.0"),
                    InetAddress.getByName("0.0.0.0"));
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }
    }

    /* Functional idioms */

    @Test
    public void testFunction() {

        Function<Customer, Boolean> isOddId = new Function<Customer, Boolean>() {

            public Boolean apply(Customer customer) {
                return customer.getId().intValue() % 2 != 0;
            }
        };

        assertTrue(isOddId.apply(customer1));
        assertFalse(isOddId.apply(customer2));
    }

    @Test
    public void testPredicate() {

        Predicate<Customer> isOddId = new Predicate<Customer>() {

            public boolean apply(Customer customer) {
                return customer.getId().intValue() % 2 != 0;
            }
        };

        assertTrue(isOddId.apply(customer1));
        assertFalse(isOddId.apply(customer2));
    }

    @Test
    public void testFunctions() {

        final String toString = "Customer{name=Name1, id=1}";

        assertEquals(customer1.toString(), toString);

        Function<Object, String> toStringFunction = Functions
                .toStringFunction();

        assertEquals(toStringFunction.apply(customer1), toString);

        Map<String, State> stateMap = Maps.newHashMap();
        stateMap.put("NY", createState());

//        Function<String, State> lookupState = Functions.forMap(stateMap);
        Function<String, State> lookupState = Functions.forMap(stateMap,
                new State("Unknown", Collections.emptySet()));
        assertEquals(lookupState.apply("no-existed").getName(), "Unknown");

        Function<State, String> getCities = new Function<State, String>() {
            @Override
            public String apply(State input) {
                return Joiner.on(",").join(input.getCities());
            }
        };

        Function<String, String> composed = Functions.compose(getCities,
                lookupState);
        String cities = getCities.apply(lookupState.apply("NY"));

        assertEquals(composed.apply("NY"), "Albany,Buffalo,NewYorkCity");
        assertEquals(composed.apply("no-existed"), "");
        assertEquals(cities, composed.apply("NY"));
    }

    @Test
    public void testPredicates() {

        Predicate<Customer> isCustomer1 = equalTo(customer1);
        Predicate<Customer> isCustomer2 = equalTo(customer2);
        Predicate<Customer> isCustomer1OrCustomer2 = or(isCustomer1,
                isCustomer2);

        ImmutableSet<Customer> customers = ImmutableSet.of(customer1,
                customer2, customer3);

        Iterable<Customer> filtered = Iterables.filter(customers,
                isCustomer1OrCustomer2);

        assertEquals(ImmutableSet.copyOf(filtered).size(), 2);

        Map<String, State> stateMap = Maps.newHashMap();
        stateMap.put("NY", createState());

        Function<String, State> lookupState = Functions.forMap(stateMap);
        Predicate<State> greaterThan = new Predicate<State>() {
            @Override
            public boolean apply(State state) {
                return state.getCities().size() > 2;
            }
        };

        Predicate<String> predicate = Predicates.compose(greaterThan,
                lookupState);
        assertTrue(predicate.apply("NY"));
    }

    @Test
    public void testSuppliers() {

        final String RESULT = "result";
        Stopwatch stopwatch = Stopwatch.createStarted();

        Supplier<String> supplier = new Supplier<String>() {
            @SneakyThrows
            @Override
            public String get() {
                TimeUnit.SECONDS.sleep(2);
                return RESULT;
            }
        };
        assertEquals(supplier.get(), RESULT);
        stopwatch.stop();
        out.printf("time: %s%n", stopwatch);

        stopwatch.reset().start();
        // Suppliers.memoizeWithExpiration(supplier, 10L, TimeUnit.SECONDS);
        Supplier<String> wrapped = Suppliers.memoize(supplier);
        assertEquals(wrapped.get(), RESULT);
        assertEquals(wrapped.get(), RESULT);
        stopwatch.stop();
        out.printf("time: %s%n", stopwatch);

        Function<Ingredients, Cake> bakeProcess = new Function<Ingredients, Cake>() {
            public Cake apply(Ingredients ingredients) {
                return new Cake(ingredients);
            }
        };

        IngredientsFactory ingredientsFactory = new IngredientsFactory();

        Supplier<Cake> cakeFactory = Suppliers.compose(bakeProcess,
                ingredientsFactory);
        cakeFactory.get();
        cakeFactory.get();
        cakeFactory.get();

        assertEquals(ingredientsFactory.getUsedNumber(), 3);

        Supplier<Ingredients> factory1 = new IngredientsFactory();
        Supplier<Ingredients> factory2 = new IngredientsFactory();

        ImmutableList<Supplier<Ingredients>> factories = ImmutableList.of(
                factory1, factory2);

        Function<Supplier<Ingredients>, Ingredients> supplierFunction = Suppliers
                .supplierFunction();

        Collection<Ingredients> ingredients = Collections2.transform(factories,
                supplierFunction);

        assertEquals(ingredients.size(), 2);
    }

    private State createState() {
        Set<City> cities = Sets.newLinkedHashSet();
        cities.add(new City("Albany"));
        cities.add(new City("Buffalo"));
        cities.add(new City("NewYorkCity"));
        return new State("NY", cities);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class State {
        private String name;
        private Set<City> cities;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class City {
        private String name;

        @Override
        public String toString() {
            return name;
        }
    }

}

class Ingredients {
}

class Cake {
    Cake(Ingredients ingredients) {
    }
}

class IngredientsFactory implements Supplier<Ingredients> {

    private int counter;

    public Ingredients get() {
        counter++;
        return new Ingredients();
    }

    int getUsedNumber() {
        return counter;
    }

}
