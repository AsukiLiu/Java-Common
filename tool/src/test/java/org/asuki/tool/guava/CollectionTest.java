package org.asuki.tool.guava;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Range.*;
import static com.google.common.collect.Sets.cartesianProduct;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.powerSet;
import static com.google.common.collect.Sets.symmetricDifference;
import static com.google.common.collect.Sets.union;
import static java.lang.String.format;
import static java.lang.System.out;
import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.BoundType;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.FluentIterable;
//import com.google.common.collect.Constraints;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import com.google.common.primitives.Ints;

public class CollectionTest {

    private Customer customer1 = new Customer(1, "Name1");
    private Customer customer2 = new Customer(2, "Name2");
    private Customer customer3 = new Customer(3, "Name3");
    private Customer customer4 = new Customer(null, "Unknown");
    private List<Customer> customers = newArrayList(
            customer1, customer2, customer3, customer4);

    @Test
    public void testImmutable1() {
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

        List<String> unmodifiableList = Collections.unmodifiableList(list);
        ImmutableList<String> immutableList = ImmutableList.copyOf(list);

        list.add("d");

        assertEquals(unmodifiableList.toString(), "[a, b, c, d]");
        assertEquals(immutableList.toString(), "[a, b, c]");

        ImmutableList<String> immutableOflist = ImmutableList.of("a", "b", "c");
        assertEquals(immutableOflist.toString(), "[a, b, c]");

        ImmutableSortedSet<String> immutableSortList = ImmutableSortedSet.of(
                "c", "b", "d", "a", "a", "b");
        assertEquals(immutableSortList.toString(), "[a, b, c, d]");

        // @formatter:off
        ImmutableSet<Customer> immutableBuilderSet = ImmutableSet.<Customer> builder()
                    .add(new Customer(11, "Google"))
                    .addAll(Lists.newArrayList(new Customer(12, "Amazon"), new Customer(13, "Facebook")))
                    .build();
        // @formatter:on

        assertEquals(immutableBuilderSet.toString(),
                "[Customer{name=Google, id=11}, Customer{name=Amazon, id=12}, Customer{name=Facebook, id=13}]");

        Map<String, String> map = Maps.newHashMap();
        map.put("11", "Google");
        map.put("12", "Amazon");

        ImmutableMap<String, String> immutableMapByBuilder = ImmutableMap
                .<String, String> builder().put("13", "Facebook").putAll(map)
                .build();
        assertEquals(immutableMapByBuilder.toString(),
                "{13=Facebook, 11=Google, 12=Amazon}");

        ImmutableMap<String, String> immutableMapByOf = ImmutableMap.of("11",
                "Google", "12", "Amazon", "13", "Facebook");
        assertEquals(immutableMapByOf.toString(),
                "{11=Google, 12=Amazon, 13=Facebook}");

        ImmutableMap<String, String> immutableMapByCopyOf = ImmutableMap
                .copyOf(map);
        assertEquals(immutableMapByCopyOf.toString(), "{11=Google, 12=Amazon}");
    }

    @Test
    public void testImmutable2() {
        ImmutableSet<String> immutableSet = ImmutableSet.of("c", "d", "a", "b");
        ImmutableList<String> immutableList = ImmutableList
                .copyOf(immutableSet);
        ImmutableSortedSet<String> immutableSortSet = ImmutableSortedSet
                .copyOf(immutableList);
        assertEquals(immutableSortSet.toString(), "[a, b, c, d]");
        assertEquals(immutableSortSet.asList().toString(), "[a, b, c, d]");

        List<Integer> list = new ArrayList<>();
        IntStream.range(0, 10).forEach(m -> list.add(m));

        ImmutableList<Integer> immutableCopylist = ImmutableList.copyOf(list
                .subList(2, 8));
        assertEquals(immutableCopylist.size(), 6);
        assertEquals(immutableCopylist.toString(), "[2, 3, 4, 5, 6, 7]");

        ImmutableSet<Integer> immutableCopySet = ImmutableSet
                .copyOf(immutableCopylist.subList(2, 3));
        assertEquals(immutableCopySet.toString(), "[4]");
    }

    @Test
    public void testLists() {

        List<String> list = Lists.asList("a", new String[] { "b", "c" });
        List<String> reverse = Lists.reverse(list);
        assertEquals(reverse.toString(), "[c, b, a]");

        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<List<Integer>> pages = Lists.partition(items, 3);
        assertEquals(pages.toString(), "[[1, 2, 3], [4, 5, 6], [7]]");

        ImmutableList<Character> characters = Lists.charactersOf("test");
        assertEquals(characters.toString(), "[t, e, s, t]");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSets() {

        ImmutableSet<Customer> customersA = ImmutableSet.of(customer1,
                customer2, customer3);
        ImmutableSet<Customer> customersB = ImmutableSet.of(customer3,
                customer4);

        assertEquals(union(customersA, customersB).size(), 4);
        assertEquals(intersection(customersA, customersB),
                ImmutableSet.of(customer3));

        List<String> list = newArrayList("aa", "bb", "cc", "cc");
        Set<String> set = newHashSet(list);
        assertEquals(set.toString(), "[aa, bb, cc]");

        Set<Integer> a = newHashSet(Arrays.asList(1, 2, 3));
        Set<Integer> b = newHashSet(Arrays.asList(3, 4, 5));

        assertEquals(intersection(a, b).toString(), "[3]");
        assertEquals(difference(a, b).toString(), "[1, 2]");
        assertEquals(symmetricDifference(a, b).toString(), "[1, 2, 4, 5]");
        assertEquals(union(a, b).toString(), "[1, 2, 3, 4, 5]");
        assertEquals(cartesianProduct(a, b).toString(),
                "[[1, 3], [1, 4], [1, 5], [2, 3], [2, 4], [2, 5], [3, 3], [3, 4], [3, 5]]");

        List<Set<Integer>> setList = newArrayList();
        powerSet(a).forEach(m -> setList.add(m));
        assertEquals(setList.toString(),
                "[[], [1], [2], [1, 2], [3], [1, 3], [2, 3], [1, 2, 3]]");
    }

    @Test
    public void testMaps() {

        SortedMap<String, String> map = new TreeMap<>();
        map.put("1", "one");
        map.put("2", "two");
        map.put("3", null);
        map.put("4", "four");

        SortedMap<String, String> filtered = Maps.filterValues(map,
                Predicates.notNull());
        assertEquals(filtered.size(), 3);

        filtered = Maps.filterKeys(map, input -> "2".equals(input));
        assertEquals(filtered.toString(), "{2=two}");

        filtered = Maps.filterEntries(map,
                new Predicate<Map.Entry<String, String>>() {
                    @Override
                    public boolean apply(Map.Entry<String, String> input) {
                        return "one".equals(input.getValue());
                    }
                });
        assertEquals(filtered.toString(), "{1=one}");

        Map<String, String> map1 = ImmutableMap.of("a", "1");
        Map<String, String> map2 = ImmutableMap.of("b", "2");
        Map<String, String> map3 = ImmutableMap.of("a", "3");
        out.println(Maps.difference(map1, map2));
        out.println(Maps.difference(map1, map3));

        Function<String, String> function = input -> input.toUpperCase();

        Set<String> set = Sets.newHashSet("a", "b", "c");
        assertEquals(Maps.asMap(set, function).toString(), "{a=A, b=B, c=C}");

        List<String> keys = Lists.newArrayList("a", "b", "c", "a");
        assertEquals(Maps.toMap(keys, function).toString(), "{a=A, b=B, c=C}");

        List<String> values = Lists.newArrayList("a", "b", "c", "d");
        assertEquals(Maps.uniqueIndex(values, function).toString(),
                "{A=a, B=b, C=c, D=d}");

        Map<String, Boolean> fromMap = ImmutableMap.of("key1", true, "key2",
                false);
        assertEquals(Maps.transformValues(fromMap, input -> !input).toString(),
                "{key1=false, key2=true}");

        Maps.EntryTransformer<String, Boolean, String> entryTransformer = new Maps.EntryTransformer<String, Boolean, String>() {
            @Override
            public String transformEntry(String key, Boolean value) {
                return value ? key : key.toUpperCase();
            }
        };
        assertEquals(Maps.transformEntries(fromMap, entryTransformer)
                .toString(), "{key1=key1, key2=KEY2}");
    }

    @Test
    public void testFluentIterable() {
        Iterable<Customer> filtered = FluentIterable.from(customers).filter(
                new Predicate<Customer>() {
                    @Override
                    public boolean apply(Customer input) {
                        return !"Unknown".equals(input.getName());
                    }
                });

        assertEquals(
                filtered.toString(),
                "[Customer{name=Name1, id=1}, Customer{name=Name2, id=2}, Customer{name=Name3, id=3}]");

        Iterable<String> transformed = FluentIterable.from(filtered).transform(
                new Function<Customer, String>() {
                    @Override
                    public String apply(Customer input) {
                        return Joiner.on("，").join(input.getId(),
                                input.getName());
                    }
                });

        assertEquals(transformed.toString(), "[1，Name1, 2，Name2, 3，Name3]");
    }

//    @Test(expectedExceptions = NullPointerException.class)
//    public void testConstraints() {
//
//        HashSet<Customer> customers = newHashSet();
//        customers.add(null);
//
//        Set<Customer> noMoreNulls = Constraints.constrainedSet(customers,
//                Constraints.notNull());
//        noMoreNulls.add(null);
//
//        fail("No exception happened!");
//    }

    @Test
    public void testMultimap() {

        Multimap<String, String> multiMap = ArrayListMultimap.create();
        multiMap.put("key1", "value1");
        multiMap.put("key1", "value1");
        multiMap.put("key2", "value3");
        multiMap.put("key2", "value4");

        assertEquals(multiMap.get("key1").toString(), "[value1, value1]");

        Multimap<String, String> hashMultimap = HashMultimap.create(multiMap);
        assertEquals(hashMultimap.get("key1").toString(), "[value1]");

        Set<String> keys = multiMap.keySet();
        assertEquals(keys.toString(), "[key1, key2]");
        Multiset<String> multiKeys = multiMap.keys();
        assertEquals(multiKeys.toString(), "[key1 x 2, key2 x 2]");

        // @formatter:off
        List<Customer> customers = Arrays.asList(
                new Customer(1, "Asuki","Tokyo"), 
                new Customer(2, "Tom", "London"), 
                new Customer(3,"Mike", "London"));
        // @formatter:on

        ListMultimap<String, Customer> customersByAddress = Multimaps.index(
                customers, new Function<Customer, String>() {
                    @Override
                    public String apply(Customer customer) {
                        return customer.getAddress();
                    }
                });

        List<Customer> tokyoCustomers = customersByAddress.get("London");
        assertEquals(tokyoCustomers.size(), 2);
    }

    @Test
    public void testMuliSet() {
        Multiset<String> multiSet = HashMultiset.create();
        multiSet.add("key");
        multiSet.add("key");

        multiSet.addAll(Arrays.asList("key", "other"));
        assertEquals(multiSet.count("key"), 3);

        List<String> words = newArrayList("cat", "dog", "cat", "dog", "cat", "cat");
        multiSet = HashMultiset.create(words);
        assertEquals(multiSet.count("cat"), 4);
        assertEquals(multiSet.count("dog"), 2);

        multiSet.remove("cat");
        assertEquals(multiSet.contains("cat"), true);
        assertEquals(multiSet.count("cat"), 3);

        multiSet.remove("cat", 3);
        assertEquals(multiSet.contains("cat"), false);
        assertEquals(multiSet.count("cat"), 0);

        assertEquals(multiSet.toString(), "[dog x 2]");
        assertEquals(multiSet.elementSet().toString(), "[dog]");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBiMap() {
        BiMap<String, String> biMap = HashBiMap.create();
        biMap.put("key", "value");

        assertEquals(biMap.get("key"), "value");

        BiMap<String, String> inversed = biMap.inverse();
        assertEquals(inversed.get("value"), "key");

        biMap.forcePut("key2", "value");
        assertEquals(biMap.get("key2"), "value");
        assertEquals(inversed.get("value"), "key2");
        assertEquals(inversed.values().toString(), "[key2]");

        biMap.put("otherKey", "value");

        fail("No exception happened!");
    }

    @Test
    public void testTable() {

        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2);
        Vertex v3 = new Vertex(3);

        // Map<Vertex, Map<Vertex, Double>> nestedMap = Maps.newHashMap();
        Table<Vertex, Vertex, Double> weightedGraph = HashBasedTable.create();
        weightedGraph.put(v1, v2, 4d);
        weightedGraph.put(v1, v3, 20d);
        weightedGraph.put(v2, v3, 5d);

        // v2 to 4, v3 to 20
        for (Map.Entry<Vertex, Double> entry : weightedGraph.row(v1).entrySet()) {
            out.println(entry.getKey() + "" + entry.getValue());
        }

        // v1 to 20, v2 to 5
        for (Map.Entry<Vertex, Double> entry : weightedGraph.column(v3)
                .entrySet()) {
            out.println(entry.getKey() + "" + entry.getValue());
        }

        Table<String, Integer, Integer> table = HashBasedTable.create();
        table.put("A", 1, 100);
        table.put("A", 2, 101);
        table.put("B", 1, 200);
        table.put("B", 2, 201);

        assertEquals(table.contains("A", 3), false);
        assertEquals(table.containsColumn(2), true);
        assertEquals(table.containsRow("A"), true);
        assertEquals(table.containsValue(201), true);

        assertEquals(table.remove("A", 2), Integer.valueOf(101));
        assertEquals(table.get("B", 2), Integer.valueOf(201));

        assertEquals(table.column(2).toString(), "{B=201}");
        assertEquals(table.row("B").toString(), "{1=200, 2=201}");

        for (Table.Cell<String, Integer, Integer> cell : table.cellSet()) {
            out.println(format("%s, %s, %s", cell.getRowKey(),
                    cell.getColumnKey(), cell.getValue()));
        }

    }

    @Test
    public void testClassToInstanceMap() {
        ClassToInstanceMap<String> stringInstanceMap = MutableClassToInstanceMap
                .create();
        ClassToInstanceMap<Customer> customerInstanceMap = MutableClassToInstanceMap
                .create();

        stringInstanceMap.put(String.class, "Google");
        assertEquals(stringInstanceMap.getInstance(String.class), "Google");

        Customer expect = new Customer(10, "Google");
        customerInstanceMap.putInstance(Customer.class, expect);

        Customer actual = customerInstanceMap.getInstance(Customer.class);
        assertEquals(actual.toString(), "Customer{name=Google, id=10}");
    }

    @Test
    public void testPeekingIterator() {
        List<String> source = newArrayList("a", "b", "b", null, "c", null, "c");

        List<String> result = newArrayList();
        PeekingIterator<String> it = Iterators
                .peekingIterator(skipNulls(source.iterator()));

        while (it.hasNext()) {
            String current = it.next();
            while (it.hasNext() && it.peek().equals(current)) {
                it.next();
            }
            result.add(current);
        }

        assertEquals(result.toString(), "[a, b, c]");
    }

    private static Iterator<String> skipNulls(Iterator<String> it) {
        return new AbstractIterator<String>() {
            @Override
            protected String computeNext() {
                while (it.hasNext()) {
                    String s = it.next();
                    if (s != null) {
                        return s;
                    }
                }
                return endOfData();
            }
        };
    }

    @Test
    public void testRangeSet() {
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(closed(1, 10));
        out.println(rangeSet);
        rangeSet.add(closedOpen(11, 15));
        out.println(rangeSet);
        rangeSet.add(open(15, 20));
        out.println(rangeSet);

        rangeSet.remove(open(5, 10));
        assertEquals(rangeSet.toString(), "[[1‥5], [10‥10], [11‥15), (15‥20)]");
    }

    @Test
    public void testRangeMap() {
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(closed(1, 10), "foo");
        out.println(rangeMap);
        rangeMap.put(open(3, 6), "bar");
        out.println(rangeMap);
        rangeMap.put(open(10, 20), "foo");
        out.println(rangeMap);

        rangeMap.remove(closed(5, 11));
        assertEquals(rangeMap.toString(), "[[1‥3]=foo, (3‥5)=bar, (11‥20)=foo]");
    }

    @Test(dataProvider = "rangeData")
    public void testRange1(Range<Integer> range, boolean isContain,
            boolean isNotContain) {

        assertEquals(range.contains(0), isContain);
        assertEquals(range.contains(10), isNotContain);
    }

    @Test
    public void testRange2() {
        assertEquals(range(1, BoundType.CLOSED, 4, BoundType.OPEN).toString(),
                "[1‥4)");

        assertEquals(closed(1, 4).containsAll(Ints.asList(1, 2, 3)), true);

        assertEquals(closedOpen(4, 4).hasLowerBound(), true);
        assertEquals(closedOpen(4, 4).hasUpperBound(), true);
        assertEquals(closedOpen(4, 4).isEmpty(), true);
        assertEquals(closed(4, 4).isEmpty(), false);

        assertEquals(open(3, 10).lowerEndpoint(), Integer.valueOf(3));
        assertEquals(closed(3, 10).upperEndpoint(), Integer.valueOf(10));
        assertEquals(closed(3, 10).lowerBoundType(), BoundType.CLOSED);
        assertEquals(open(3, 10).upperBoundType(), BoundType.OPEN);

        assertEquals(open(1, 4).encloses(closedOpen(2, 4)), true);
        assertEquals(open(1, 4).encloses(closedOpen(2, 5)), false);

        assertEquals(closed(3, 5).isConnected(open(5, 10)), true);
        assertEquals(closed(0, 9).isConnected(closed(3, 4)), true);
        assertEquals(open(3, 5).isConnected(open(5, 10)), false);

        assertEquals(closed(3, 5).intersection(open(5, 10)).toString(), "(5‥5]");
        assertEquals(closed(0, 5).intersection(closed(3, 9)).toString(),
                "[3‥5]");

        assertEquals(closed(3, 5).span(open(5, 10)).toString(), "[3‥10)");
        assertEquals(closed(1, 5).span(closed(7, 10)).toString(), "[1‥10]");

        Function<Customer, Integer> idFunction = input -> input.getId();
        Range<Integer> idRange = Range.closedOpen(100, 300);
        Predicate<Customer> predicate = Predicates.compose(idRange, idFunction);

        assertEquals(predicate.apply(new Customer(301, "Andy")), false);
        assertEquals(predicate.apply(new Customer(101, "Tom")), true);
    }

    @DataProvider
    private Object[][] rangeData() {
        // @formatter:off
        return new Object[][] { 
                { closed(0, 10), true, true },
                { closedOpen(0, 10), true, false },
                { open(0, 10), false, false },
                { openClosed(0, 10), false, true } 
        };
        // @formatter:on
    }

    @AllArgsConstructor
    @ToString
    private static class Vertex {

        @Getter
        @Setter
        private int mark;
    }

}
