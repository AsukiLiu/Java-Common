package org.asuki.tool.jackson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asuki.tool.jackson.model.Item;
import org.asuki.tool.jackson.model.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Jackson2ViewTest {

    private ObjectMapper mapper;

    @BeforeMethod
    public void init() {
        mapper = new ObjectMapper();
    }

    public static class Views {
        public static class Public {
        }

        public static class Internal extends Public {
        }
    }

    @Test
    public void testSerialize() throws Exception {
        User user = new User(1, "Andy");

        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        String userAsString = mapper
                .writerWithView(Views.Public.class)
                .writeValueAsString(user);

        out.println(userAsString);

        assertThat(userAsString, containsString("Andy"));
        assertThat(userAsString, not(containsString("1")));
    }

    @Test
    public void testSerialize2() throws Exception {
        Item item = new Item(1, "ItemA", new User(2, "UserA"));

        String itemAsString = mapper
                .writerWithView(Views.Public.class)
//                .writerWithView(Views.Internal.class)
                .writeValueAsString(item);

        out.println(itemAsString);

        assertThat(itemAsString, containsString("itemName"));
        assertThat(itemAsString, not(containsString("owner")));
//        assertThat(itemAsString, containsString("owner"));
    }

    @Test
    public void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Andy\"}";

        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        User user = mapper
                .readerWithView(Views.Public.class)
//                .readerWithView(Views.Internal.class)
                .forType(User.class)
//                .withType(User.class)
                .readValue(json);

        out.println(user);

        assertThat(user.getId(), is(not(1)));
        assertThat(user.getName(), is("Andy"));
    }

}

