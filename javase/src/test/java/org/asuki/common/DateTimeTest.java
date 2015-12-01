package org.asuki.common;

import static java.lang.System.out;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import org.testng.annotations.Test;

public class DateTimeTest {

    @Test
    public void testClock() {
        Clock clock = Clock.systemUTC(); // systemDefaultZone()
        assertThat(clock.millis(), is(System.currentTimeMillis()));

        Instant instant = clock.instant();
        Date legacyDate = Date.from(instant);
        out.println(clock.instant());
        out.println(legacyDate);
    }

    @Test
    public void testTimeZone() {
        out.println(ZoneId.getAvailableZoneIds());
        ZoneId zone = ZoneId.of("Europe/Berlin");
        out.println(zone.getRules());

        Clock clock = Clock.systemUTC();

        out.println(ZonedDateTime.now());
        out.println(ZonedDateTime.now(clock));
        out.println(ZonedDateTime.now(ZoneId.of("Europe/Berlin")));
    }

    @Test
    public void testLocalTime() {
        Clock clock = Clock.systemUTC();

        out.println(LocalTime.now());
        out.println(LocalTime.now(clock));

        LocalTime now1 = LocalTime.now(ZoneId.of("Europe/Berlin"));
        LocalTime now2 = LocalTime.now(ZoneId.of("Asia/Tokyo"));

        out.println(now1.isBefore(now2));

        long hoursBetween = ChronoUnit.HOURS.between(now1, now2);
        long minutesBetween = ChronoUnit.MINUTES.between(now1, now2);

        out.println(hoursBetween);
        out.println(minutesBetween);

        out.println(LocalTime.of(23, 59, 59)); // 23:59:59

        DateTimeFormatter formatter = ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(Locale.JAPAN);

        out.println(LocalTime.parse("22:45", formatter));

        LocalTime time = LocalTime.now();
        LocalTime newTime = time.plusHours(2);
        out.println(newTime);
    }

    @Test
    public void testLocalDate() {
        Clock clock = Clock.systemUTC();

        out.println(LocalDate.now());
        out.println(LocalDate.now(clock));

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
        LocalDate yesterday = tomorrow.minusDays(2);
        out.println(yesterday);

        out.printf("Year: %d, Month: %d, Day: %d %n",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        LocalDate day = LocalDate.of(2014, Month.APRIL, 25);
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        out.println(dayOfWeek);

        DateTimeFormatter formatter = ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.GERMAN);

        // 2014-04-25
        out.println(LocalDate.parse("25.04.2014", formatter));
        out.println(LocalDate.parse("20140425", DateTimeFormatter.BASIC_ISO_DATE));

        LocalDate dateOfBirth = LocalDate.of(2010, 02, 15);
        MonthDay birthday = MonthDay.of(dateOfBirth.getMonth(), dateOfBirth.getDayOfMonth());
        MonthDay currentMonthDay = MonthDay.from(today);
        out.println(currentMonthDay.equals(birthday));

        YearMonth currentYearMonth = YearMonth.now();
        out.printf("%s(%d)%n", currentYearMonth, currentYearMonth.lengthOfMonth());
        YearMonth creditCardExpiry = YearMonth.of(2018, Month.FEBRUARY);
        out.printf("Credit card expires on %s %n", creditCardExpiry);

        out.println(today.isLeapYear());
    }

    @Test
    public void testLocalDateTime() {
        Clock clock = Clock.systemUTC();

        out.println(LocalDateTime.now());
        out.println(LocalDateTime.now(clock));

        LocalDateTime dateTime = LocalDateTime.of(2014, Month.APRIL, 25, 23,
                59, 59);

        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        Month month = dateTime.getMonth();
        long minuteOfDay = dateTime.getLong(ChronoField.MINUTE_OF_DAY);

        out.println(dayOfWeek);
        out.println(month);
        out.println(minuteOfDay);

        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date legacyDate = Date.from(instant);
        out.println(legacyDate);

        DateTimeFormatter formatter = ofPattern("MM dd, yyyy - HH:mm");

        LocalDateTime parsed = LocalDateTime.parse("04 25, 2014 - 01:03",
                formatter);

        out.println(formatter.format(parsed));
    }

    @Test
    public void testDuration() {

        LocalDateTime from = LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2015, Month.JANUARY, 1, 23, 59, 59);

        Duration duration = Duration.between(from, to);
        assertThat(duration.toDays(), is(365L));
    }

    @Test
    public void testPeriod() {
        LocalDate today = LocalDate.now();
        LocalDate java8Release = LocalDate.of(2014, Month.MARCH, 18);

        Period period = Period.between(today, java8Release);
        out.println(period.getMonths());
    }
}
