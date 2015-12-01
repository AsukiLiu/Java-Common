package org.asuki.tool.joda;

import org.joda.time.*;
import org.joda.time.DateTime.Property;
import org.joda.time.format.DateTimeFormat;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.TimeZone;

import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class BasicDateTimeTest {
    @Test
    public void testTimeZone() {
        out.println(DateTimeZone.getDefault());

        out.println(new LocalDate());
        out.println(new LocalTime());
        out.println(new LocalDateTime());
        out.println(new DateTime());

        DateTime dateTime = new DateTime(DateTimeZone.UTC);
        out.println(dateTime);

        DateTime dateTime1 = new DateTime(dateTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        DateTime dateTime2 = new DateTime(dateTime, DateTimeZone.getDefault());
        out.println(dateTime1);

        assertThat(dateTime1.equals(dateTime2), is(true));
        assertThat(dateTime1.isEqual(dateTime2), is(true));

        assertThat(dateTime1.equals(dateTime), is(false));
        assertThat(dateTime1.isEqual(dateTime), is(true));
    }

    @Test
    public void testConvert() {
        Date date = new Date();
        DateTime dateTime = new DateTime(date);
        Date convertDate = dateTime.toDate();

        assertThat(date.equals(convertDate), is(true));
    }

    @Test
    public void testFormat() {
        DateTime dateTime = new DateTime();
        out.println(dateTime.toString("yyyy-MM-dd HH:mm:ss"));

        dateTime = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2015-12-02");
        out.println(dateTime);
    }

    @Test
    public void testCompute() {
        DateTime dateTime = new DateTime();
        out.println(dateTime);

        DateTime yesterday = dateTime.minusDays(1);
        out.println(yesterday);
        DateTime tomorrow = dateTime.plusDays(1);
        out.println(tomorrow);
        DateTime afterThreeMonth = dateTime.plusMonths(3);
        out.println(afterThreeMonth);
        DateTime nextYear = dateTime.plusYears(1);
        out.println(nextYear);

        Property dayOfMonth = dateTime.dayOfMonth();
        DateTime firstDayOfMonth = dayOfMonth.withMinimumValue();
        DateTime lastDayOfMonth = dayOfMonth.withMaximumValue();
        out.printf("%s - %s \n", firstDayOfMonth, lastDayOfMonth);

        Property dayOfWeek = dateTime.dayOfWeek();
        DateTime firstDayOfWeek = dayOfWeek.withMinimumValue();
        DateTime lastDayOfWeek = dayOfWeek.withMaximumValue();
        out.printf("%s - %s \n", firstDayOfWeek, lastDayOfWeek);
    }
}
