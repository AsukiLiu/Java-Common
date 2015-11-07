package org.asuki.common;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import org.asuki.common.model.Person;
import org.asuki.common.model.Student;
import org.asuki.common.util.StreamUtil;
import org.asuki.common.util.StreamUtil.Pair;
import org.testng.annotations.Test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

public class StreamTest {

    @Test
    public void testSourceOperation() {

        Stream.generate(Date::new).limit(5).forEach(p -> out.println(p));

        Object[] powers = Stream.iterate(1.0, p -> p * 2)
                .peek(e -> out.println(e)).limit(10).toArray();
        assertThat(powers.length, is(10));

        List<String> words = Stream.of("a", "b", "b", "c", "a", "d").distinct()
                .collect(Collectors.toList());
        assertThat(words.toString(), is("[a, b, c, d]"));

        Optional<String> maxWord = words.stream().max(
                String::compareToIgnoreCase);
        assertThat(maxWord.isPresent(), is(true));
        assertThat(maxWord.get(), is("d"));

        Optional<String> startsWithC = words.stream()
                .filter(s -> s.startsWith("c")).findFirst();
        assertThat(startsWithC.isPresent(), is(true));

        startsWithC = words.stream().parallel().filter(s -> s.startsWith("c"))
                .findAny();
        assertThat(startsWithC.isPresent(), is(true));

        boolean isStartsWithC = words.stream().parallel()
                .anyMatch(s -> s.startsWith("c"));
        assertThat(isStartsWithC, is(true));

        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());
        // @formatter:off
        Map<String, String> languageNames = locales.collect(
            Collectors.toMap(
                locale -> locale.getDisplayLanguage(), 
                locale -> locale.getDisplayLanguage(locale),
                (oldValue, newValue) -> oldValue));
        // @formatter:on
        out.println(languageNames);

        locales = Stream.of(Locale.getAvailableLocales());
        Map<String, List<Locale>> countryToLocales = locales.collect(Collectors
                .groupingBy(Locale::getCountry));
        List<Locale> swissLocales = countryToLocales.get("CH");
        assertThat(swissLocales.toString(), is("[fr_CH, de_CH, it_CH]"));

        assertThat(
                eachByStreamBuilder("abcd").collect(Collectors.joining("-")),
                is("a-b-c-d"));
        assertThat(
                eachBySurrogatePair("abcd").collect(Collectors.joining("-")),
                is("a-b-c-d"));
    }

    private static Stream<String> eachByStreamBuilder(String str) {
        Stream.Builder<String> sb = Stream.builder();
        for (int i = 0; i < str.length(); i++) {
            sb.add(str.charAt(i) + "");
        }
        return sb.build();
    }

    private static Stream<String> eachBySurrogatePair(String str) {
        // str.chars()
        return str.codePoints().boxed()
                .map(i -> String.valueOf(Character.toChars(i)));
    }

    @Test
    public void testIntermediateOperation() {
        {
            final List<String> list = ImmutableList.of("sa", "bb", "sc");

            // s -> s.toUpperCase()
            List<String> newWay = list.stream().filter(s -> s.startsWith("s"))
                    .map(String::toUpperCase).collect(Collectors.toList());

            assertThat(newWay.toString(), is("[SA, SC]"));

            list.stream().map(String::toUpperCase)
                    .sorted((a, b) -> b.compareTo(a)).forEach(out::println);
        }

        List<Integer> nums = IntStream.range(0, 5).boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        assertThat(nums.toString(), is("[0, 1, 2, 3, 4]"));

        {
            List<List<String>> collection = asList(asList("aa", "bb"),
                    asList("cc", "dd", "ee"));

            List<String> list = collection.stream()
                    .flatMap(value -> value.stream())
                    .collect(Collectors.toList());

            assertThat(list.toString(), is("[aa, bb, cc, dd, ee]"));
        }

        {
            Function<Integer, Stream<Integer>> repeat = n -> Stream.generate(
                    () -> n).limit(n);

            List<Integer> list = Arrays.asList(0, 1, 2, 4).stream()
                    .flatMap(repeat)
                    // .flatMap(repeat)
                    .collect(Collectors.toList());

            assertThat(list.toString(), is("[1, 2, 2, 4, 4, 4, 4]"));
        }

        {
            Function<Integer, Stream<String>> dual = i -> IntStream.range(0, 3)
                    .boxed().map(j -> i + ":" + j);

            List<String> list = IntStream.range(0, 3).boxed().flatMap(dual)
                    .collect(Collectors.toList());

            assertThat(list.toString(),
                    is("[0:0, 0:1, 0:2, 1:0, 1:1, 1:2, 2:0, 2:1, 2:2]"));
        }

        {
            List<Person> persons = Arrays.asList(new Person("Tom", 27),
                    new Person("John", 38));

            double avg1 = persons.stream().reduce(0.0,
                    (acc, u) -> acc + u.getAge(), (r1, r2) -> r1 + r2)
                    / persons.size();

            double avg2 = persons.stream().collect(
                    Collectors.averagingInt(Person::getAge));

            double avg3 = persons.stream().mapToInt(Person::getAge).average()
                    .getAsDouble();

            assertThat(avg1, is(avg2));
            assertThat(avg1, is(avg3));
        }
    }

    @Test
    public void testTerminalOperation() {
        final List<String> list = ImmutableList.of("sa", "bb", "sc");

        Predicate<String> predicate = s -> s.startsWith("s");

        assertThat(list.stream().anyMatch(predicate), is(true));

        assertThat(list.stream().allMatch(predicate), is(false));

        assertThat(list.stream().noneMatch(predicate), is(false));

        assertThat(list.stream().filter(predicate).count(), is(2L));

        assertThat(list.stream().filter(predicate).findFirst().get(), is("sa"));

        Optional<String> reduced = list.stream().sorted()
                .reduce((s1, s2) -> s1 + "#" + s2);

        reduced.ifPresent(out::println);

        assertThat(reduced.get(), is("bb#sa#sc"));

        String str = Stream.of("AB", "CD", "EF").reduce((a, b) -> b + a).get();
        assertThat(str, is("EFCDAB"));
    }

    @Test
    public void testConvertToCollection() {

        // @formatter:off
        List<Person> persons = Arrays.asList(
                new Person("Tom", 20), 
                new Person("John", 40), 
                new Person("John", 30));
        // @formatter:on

        Optional<Person> minAgePerson = persons.stream().min(
                (p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()));
        assertThat(minAgePerson.isPresent(), is(true));
        assertThat(minAgePerson.get().toString(),
                is("Person(name=Tom, age=20)"));

        Map<Integer, String> peoples = persons.stream().collect(
                Collectors.toMap(Person::getAge, Person::getName));
        assertThat(peoples.toString(), is("{20=Tom, 40=John, 30=John}"));

        ConcurrentMap<String, List<Person>> result = persons.stream()
                .parallel()
                .collect(Collectors.groupingByConcurrent(Person::getName));
        assertThat(result.get("Tom").size(), is(1));
        assertThat(result.get("John").size(), is(2));

        {
            Stream<String> str = persons.stream().map((p) -> p.getName());

            assertThat(str.collect(Collectors.joining()), is("TomJohnJohn"));

            str = persons.stream().map((p) -> p.getName());
            assertThat(str.collect(Collectors.joining(", ")),
                    is("Tom, John, John"));

            str = persons.stream().map((p) -> p.getName());
            assertThat(str.collect(Collectors.joining(", ", "[", "]")),
                    is("[Tom, John, John]"));
        }

        List<String> nameList = persons.stream().map((p) -> p.getName())
                .distinct().collect(Collectors.toList());
        
        String[] namesArray = persons.stream().map((p) -> p.getName())
                .distinct().toArray(String[]::new);

        assertThat(nameList.size(), is(2));
        assertThat(nameList.toString(), is(Arrays.toString(namesArray)));

        IntSummaryStatistics stats = persons.stream()
                .mapToInt((p) -> p.getAge()).summaryStatistics();

        assertThat(
                stats.toString(),
                is("IntSummaryStatistics{count=3, sum=90, min=20, average=30.000000, max=40}"));

        {
            int sumAge1 = persons.stream().collect(
                    Collectors.summingInt(p -> p.getAge()));

            // TODO An unknown compilation problem occurred (1.8.0_25)
//            int sumAge2 = persons.stream().reduce(0,
//                    (acc, u) -> acc + u.getAge(), (a1, a2) -> a1 + a2);
//
//            assertThat(sumAge1, is(sumAge2));
        }

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);

        {
            Map<Boolean, List<Integer>> map = list.stream().collect(
                    Collectors.partitioningBy(n -> n % 2 == 0));

            assertThat(map.toString(), is("{false=[1, 3, 5], true=[2, 4, 6]}"));
        }

        {
            Map<Integer, Double> map = list.stream().collect(
                    Collectors.groupingBy(n -> n % 3,
                            Collectors.averagingDouble(n -> (int) n + 0.0)));

            assertThat(map.toString(), is("{0=4.5, 1=2.5, 2=3.5}"));
        }

        {
            List<String> members = Arrays.asList("b0001", "a0001", "d0002",
                    "c0003", "a0002");

            Map<Character, List<String>> group = members.stream().collect(
                    Collectors.groupingBy(s -> s.charAt(0), LinkedHashMap::new,
                            Collectors.toList()));

            group.forEach((k, v) -> out.println(k + " = " + v));
        }

    }

    @Test
    public void testCustomCollect() {
        String[] strings = { "aaa", "bbb", "ccc" };

        List<String> list = Stream.of(strings).collect(ArrayList::new,
                ArrayList::add, ArrayList::addAll);

        assertThat(list.toString(), is("[aaa, bbb, ccc]"));

        String concat = Stream
                .of(strings)
                .collect(StringBuilder::new, StringBuilder::append,
                        StringBuilder::append).toString();

        assertThat(concat, is("aaabbbccc"));
    }

    @Test
    public void testYieldLike() throws NoSuchAlgorithmException {
        final int RANDOM_INTS = 5;

        try (Stream<Integer> randomInt = generateRandomIntStream()) {
            randomInt.limit(RANDOM_INTS).sorted().forEach(out::println);
        }
    }

    // Entity to DTO
    @Test
    public void testMapping() {
        List<Person> persons = newArrayList();
        persons.add(new Person("Tom", 30));
        persons.add(new Person("Jack", 25));

        List<PersonDto> personDtos1 = StreamSupport
                .stream(persons.spliterator(), false).map(PersonDto::new)
                .collect(Collectors.toList());

        List<PersonDto> personDtos2 = persons.stream().map(PersonDto::new)
                .collect(Collectors.toList());

        assertThat(personDtos1.toString(), is(personDtos2.toString()));
    }

    @Test
    public void testParallelCaseA() {

        List<Integer> numbers = ImmutableList.of(9, 5, 10);
        assertThat(numbers.stream().sorted().collect(Collectors.toList())
                .toString(), is("[5, 9, 10]"));
        assertThat(
                numbers.parallelStream().sorted().collect(Collectors.toList())
                        .toString(), is("[5, 9, 10]"));

        Map<Boolean, List<Integer>> groupBy = numbers.parallelStream().collect(
                Collectors.groupingBy(s -> s > 6));
        assertThat(groupBy.toString(), is("{false=[5], true=[9, 10]}"));

        Integer[] filtered = numbers.parallelStream().filter(s -> s > 6)
                .toArray(Integer[]::new);
        assertThat(asList(filtered).toString(), is("[9, 10]"));

        "hello world".chars().forEach(c -> out.print((char) c));
        out.println();
        // Not preserve the order
        "hello world".chars().parallel().forEach(c -> out.print((char) c));
        out.println();

        Stopwatch stopwatch = Stopwatch.createStarted();
        IntStream.range(0, 10).forEach(m -> longRunProcess(m));
        stopwatch.stop();
        out.printf(" Time: %s%n", stopwatch);

        stopwatch = Stopwatch.createStarted();
        IntStream.range(0, 10).parallel().forEach(m -> longRunProcess(m));
        stopwatch.stop();
        // <10s
        out.printf(" Time: %s%n", stopwatch);
    }

    @Test
    public void testParallelCaseB() {
        List<Person> persons = asList(new Person("Joe"), new Person("Jim"),
                new Person("John"));
        persons.forEach(p -> p.setAge(20));

        // @formatter:off
        
        // .map(person -> new Student(person))
        List<Student> students = persons.stream()
                .filter(p -> p.getAge() > 18)
                .map(Student::new)
                .collect(Collectors.toList());

        students = persons.stream()
                .parallel()
                .filter(p -> p.getAge() > 18)
                .sequential()
                .map(Student::new)
                .collect(Collectors.toCollection(ArrayList::new));
        
        // @formatter:on

        assertThat(
                students.toString(),
                is("[Student{name=Joe, age=20}, Student{name=Jim, age=20}, Student{name=John, age=20}]"));
    }

    @Test
    public void testArrayParallel() {
        long[] array = new long[2000];

        Arrays.parallelSetAll(array, index -> ThreadLocalRandom.current()
                .nextInt(10000));
        Arrays.stream(array).limit(10).forEach(i -> out.print(i + " "));

        out.println();

        Arrays.parallelSort(array);
        Arrays.stream(array).limit(10).forEach(i -> out.print(i + " "));

        out.println();
    }

    @Test
    public void testFiles() throws IOException {
        // Path path = new File("pom.xml").toPath();
        // Path path = Paths.get("pom.xml");
        FileSystem fs = FileSystems.getDefault();
        Path path = fs.getPath("pom.xml");

        List<String> lines = new ArrayList<>();

        Consumer<String> replace = l -> lines.add(l.replaceAll("<", "(")
                .replaceAll(">", ")"));

        // ★Approach one
        // Files.lines(path, StandardCharsets.UTF_8).forEach(replace);
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.onClose(() -> out.println("Done!")).forEach(replace);
        }

        int size1 = lines.size();

        lines.clear();

        // ★Approach two
        try (BufferedReader br = Files.newBufferedReader(path,
                Charset.forName("UTF-8"))) {
            br.lines().forEach(replace);
        }

        int size2 = lines.size();

        List<String> allLines = Files
                .readAllLines(path, StandardCharsets.UTF_8);

        assertThat(size1, is(allLines.size()));
        assertThat(size2, is(allLines.size()));
    }

    @Test
    public void testFiles2() throws IOException {
        Path path = Paths.get("pom.xml");

        // Find the 5 most frequent words in a file
        List<String> words = Files.lines(path)
            .parallel()
            .flatMap(line -> Arrays.asList(line.split("\\b")).stream())
            .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
            .entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry<String, Long>::getValue).reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        words.forEach(out::println);
    }

    @Test
    public void testStreamUtil() {
        Consumer<Pair<Integer, Integer>> print = p -> out.println(format(
                "First:%d, Second:%d [%s]", p.getFirst(), p.getSecond(),
                Thread.currentThread()));

        {
            List<Integer> list1 = Arrays.asList(0, 1, 1, 0, 1);
            List<Integer> list2 = Arrays.asList(1, 1, 0, 0, 1);

            Optional<Pair<Integer, Integer>> hit = StreamUtil
                    .zip(list1.stream(), list2.stream())
                    .peek(print)
                    .filter(p -> p.getFirst().equals(1)
                            && p.getFirst().equals(p.getSecond())).findFirst();

            assertThat(hit.isPresent() ? hit.get().toString() : "none",
                    is("StreamUtil.Pair(first=1, second=1)"));
        }

        {
            List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6);
            int sum = StreamUtil
                    .zip(input.stream(), Stream.iterate(0, n -> n + 1))
                    .parallel()
                    // .peek(print)
                    .filter(p -> p.getSecond() % 2 == 1)
                    .collect(Collectors.summingInt(p -> p.getFirst()));

            assertThat(sum, is(2 + 4 + 6));
        }

        {
            print = p -> out.println(format("First:%c, Second:%c", (char) p
                    .getFirst().intValue(), (char) p.getSecond().intValue()));

            String str = "ABCAABBCCCDEEF";
            long count = StreamUtil
                    .zip(str.chars().boxed(), str.chars().skip(1).boxed(),
                            str.length() - 1)
                    .peek(print)
                    .filter(p -> p.getFirst().equals(p.getSecond())).count();

            assertThat(count, is(5L));
        }
    }

    @Test
    public void testExample() {
        Collection<Task> tasks = Arrays.asList(
                new Task(Status.OPEN, 6),
                new Task(Status.OPEN, 7), 
                new Task(Status.CLOSE, 8));

        int totalPointsOfOpenTasks = tasks.stream()
                .filter(task -> task.getStatus() == Status.OPEN)
                .mapToInt(Task::getPoints)
                .sum();

        assertThat(totalPointsOfOpenTasks, is(6 + 7));

        int totalPoints = tasks.stream()
                .parallel()
                .map(task -> task.getPoints()) // Task::getPoints
                .reduce(0, Integer::sum);

        assertThat(totalPoints, is(6 + 7 + 8));

        Map<Status, List<Task>> map = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        out.println(map);

        // @formatter:off
        Collection<String> result = tasks.stream()                      // Stream<String>
                .mapToInt(Task::getPoints)                               // IntStream
                .asLongStream()                                          // LongStream
                .mapToDouble(points -> points / (double) totalPoints)  // DoubleStream
                .boxed()                                                 // Stream<Double>
                .mapToLong(weigth -> (long) (weigth * 100))             // LongStream
                .mapToObj(percentage -> percentage + "%")               // Stream<String>
                .collect(Collectors.toList());                           // List<String>
        // @formatter:on

        out.println(result);
    }

    @Test
    public void testExample2() {
        int[] array = { 5, 5, 6, 7, 8, 8, 8, 8, 9 };

        IntStream.of(array)
                .boxed()                        // Stream<Integer>
                .collect(groupingBy(i -> i))    // Map<Integer, List<Integer>>
                .entrySet()                     // Set<Entry<Integer, List<Integer>>>
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(
                        e -> {
                            out.println(format(
                                    "Duplicate:%s, Sum:%s",
                                    e.getKey(),
                                    e.getValue().stream()
                                            .collect(summingInt(i -> i))));
                        });
    }

    @ToString
    private static class PersonDto {

        private int age;
        private String name;

        public PersonDto(Person person) {
            this.name = person.getName();
            this.age = person.getAge();
        }
    }
    
    @SneakyThrows
    private <T> void longRunProcess(T t) {
        TimeUnit.SECONDS.sleep(1);

        out.print(t);
    }

    private static Stream<Integer> generateRandomIntStream()
            throws NoSuchAlgorithmException {

        return Stream.generate(new Supplier<Integer>() {

            final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            boolean init = false;
            int num = 0;

            @Override
            public Integer get() {
                if (!init) {
                    random.setSeed(new Date().getTime());
                    init = true;
                    out.println("init");
                }
                final int nextInt = random.nextInt();
                out.println(format("Generated random %d: %d", num++, nextInt));
                return nextInt;
            }

        });
    }
}

enum Status {
    OPEN, CLOSE
};

@AllArgsConstructor
@ToString
class Task {
    @Getter
    private final Status status;
    @Getter
    private final Integer points;
}
