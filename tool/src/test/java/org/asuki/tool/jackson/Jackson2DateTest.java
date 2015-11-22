package org.asuki.tool.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Jackson2DateTest {

    public static final String DATE_FORMAT = "dd-MM-yyyy hh:mm:ss";
    public static final String DATE_FORMAT2 = "yyyy-MM-dd HH:mm:ss";

    private ObjectMapper mapper;
    private SimpleDateFormat dateFormat;

    @BeforeMethod
    public void init() {
        mapper = new ObjectMapper();
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    @Test(dataProvider = "data")
    public void test(boolean disable, String expect) throws Exception {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = dateFormat.parse("22-11-2015 02:30:50");
        Event event = new Event("party", date);

        if (disable) {
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        String result = mapper.writeValueAsString(event);
        assertThat(result, containsString(expect));
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][] {
                { false, "1448159450000" },
                { true, "2015-11-22T02:30:50.000+0000" },
        };
    }

    @Test
    public void testFormatCustom() throws Exception {
        // @JsonFormat is required
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String toParse = "22-11-2015 02:30:50";
        Date date = dateFormat.parse(toParse);
        AnnotationEvent event = new AnnotationEvent("party", date);

        // @JsonSerialize is unnecessary
//        mapper.setDateFormat(dateFormat);

        String result = mapper.writeValueAsString(event);
        assertThat(result, containsString(toParse));
    }

    @Test
    public void testJoda() throws Exception {
        DateTime date = new DateTime(2015, 11, 22, 2, 30, 50, DateTimeZone.forID("Europe/London"));

        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String result = mapper.writeValueAsString(date);
        assertThat(result, containsString("2015-11-22T02:30:50.000Z"));
    }

    @Test
    public void testJodaCustom() throws Exception {
        DateTime date = new DateTime(2015, 11, 22, 2, 30, 50);
        JodaEvent event = new JodaEvent("party", date);

        String result = mapper.writeValueAsString(event);
        assertThat(result, containsString("2015-11-22 02:30:50"));
    }

    @Test
    public void testJava8() throws Exception {
        LocalDateTime date = LocalDateTime.of(2015, 11, 22, 2, 30, 50);

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String result = mapper.writeValueAsString(date);
        assertThat(result, containsString("2015-11-22T02:30:50"));
    }

    @Test
    public void testJava8Custom() throws Exception {
        LocalDateTime date = LocalDateTime.of(2015, 11, 22, 2, 30, 50);
        Java8Event event = new Java8Event("party", date);

        String result = mapper.writeValueAsString(event);
        assertThat(result, containsString("2015-11-22 02:30:50"));
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"name\":\"party\",\"date\":\"22-11-2015 02:30:50\"}";

        // @JsonDeserialize is unnecessary
//        mapper.setDateFormat(dateFormat);

        AnnotationEvent event = mapper.readValue(json, AnnotationEvent.class);
        assertThat(dateFormat.format(event.getDate()), is("22-11-2015 02:30:50"));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Event {
        private String name;
        private Date date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AnnotationEvent {
        private String name;

//        @JsonFormat(shape = Shape.STRING, pattern = DATE_FORMAT)
        @JsonSerialize(using = CustomDateSerializer.class)
        @JsonDeserialize(using = CustomDateDeserializer.class)
        private Date date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class JodaEvent {
        private String name;

        @JsonSerialize(using = CustomDateTimeSerializer.class)
        private DateTime date;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Java8Event {
        private String name;

        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime date;
    }

    private static class CustomDateSerializer extends JsonSerializer<Date> {
        private static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        @Override
        public void serialize (Date value, JsonGenerator gen, SerializerProvider arg2)
                throws IOException {
            gen.writeString(formatter.format(value));
        }
    }

    private static class CustomDateTimeSerializer extends JsonSerializer<DateTime> {
        private static DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT2);

        @Override
        public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2)
                throws IOException {
            gen.writeString(formatter.print(value));
        }
    }

    private static class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        private static java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT2);

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2)
                throws IOException {
            gen.writeString(formatter.format(value));
        }
    }

    private static class CustomDateDeserializer extends JsonDeserializer<Date> {
        private static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        @Override
        public Date deserialize(JsonParser parser, DeserializationContext context)
                throws IOException {
            String date = parser.getText();
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
