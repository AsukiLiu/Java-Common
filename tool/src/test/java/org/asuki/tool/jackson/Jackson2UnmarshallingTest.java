package org.asuki.tool.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.ToString;
import org.asuki.tool.jackson.model.Item;
import org.asuki.tool.jackson.model.MyDto;
import org.asuki.tool.jackson.model.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Jackson2UnmarshallingTest {

    private ObjectMapper mapper;

    @BeforeMethod
    public void init() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testJsonNode() throws Exception {
        String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";

        JsonNode actualObj = mapper.readTree(jsonString);

        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonString);
        actualObj = mapper.readTree(parser);

        out.print(actualObj);

        JsonNode jsonNode1 = actualObj.get("k1");
        assertThat(jsonNode1.textValue(), equalTo("v1"));
    }

    @Test
    public void testUnknown() throws Exception {
        String jsonAsString =
                "{\"stringValue\":\"a\"," +
                "\"bVal\":true}";

        MyDto dto = mapper.readValue(jsonAsString, MyDto.class);
        out.println(dto);

        jsonAsString =
                "{\"stringValue\":\"a\"," +
                "\"intValue\":1," +
                "\"bVal\":true," +
                "\"notExist\":\"something\"}";

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        dto = mapper.readValue(jsonAsString, MyDto.class);
        out.print(dto);

        assertThat(dto.getStringValue(), equalTo("a"));
        assertThat(dto.isBooleanValue(), equalTo(true));
        assertThat(dto.getIntValue(), equalTo(1));
    }

    @Test
    public void testUnmarshallToArrayOrList() throws Exception {
        List<MyDto> dtos = newArrayList(
                new MyDto("a", 1, true), new MyDto("b", 2, false));

        String jsonArray = mapper.writeValueAsString(dtos);
        out.println(jsonArray);

        MyDto[] asArray = mapper.readValue(jsonArray, MyDto[].class);
        out.println(Arrays.toString(asArray));

        assertThat(asArray[0], instanceOf(MyDto.class));

        List<MyDto> asList = mapper.readValue(jsonArray, List.class);

        assertThat(asList.get(0), not(instanceOf(MyDto.class)));
        assertThat(asList.get(0), instanceOf(LinkedHashMap.class));

        asList = mapper.readValue(jsonArray, new TypeReference<List<MyDto>>() {});
//        asList = mapper.reader()
//                .withType(new TypeReference<List<MyDto>>() {})
//                .readValue(jsonArray);
        assertThat(asList.get(0), instanceOf(MyDto.class));

        CollectionType type =
                mapper.getTypeFactory().constructCollectionType(List.class, MyDto.class);
        asList = mapper.readValue(jsonArray, type);
        assertThat(asList.get(0), instanceOf(MyDto.class));
    }

    @Test
    public void testCustom() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"itemName\": \"ItemA\",\n" +
                "    \"createdBy\": 2\n" +
                "}";

        // Custom Deserializer on the Class

        Item item = mapper.readValue(json, Item.class);
        out.println(item);

        // Custom Deserializer on ObjectMapper

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Item.class, new ItemDeserializer());
        mapper.registerModule(module);

        item = mapper.readValue(json, Item.class);
        out.println(item);
    }

    public static class ItemDeserializer extends JsonDeserializer<Item> {
        @Override
        public Item deserialize(JsonParser parser, DeserializationContext ctxt)
                throws IOException {

            JsonNode node = parser.getCodec().readTree(parser);
            int itemId = (Integer) node.get("id").numberValue();
            String itemName = node.get("itemName").asText();
            int userId = (Integer) node.get("createdBy").numberValue();

            return new Item(itemId, itemName, new User(userId, null));
        }
    }

    // JsonMappingException: Root name does not match expected
    @Test
    public void testRoot() throws Exception {
        String json = "{\"user\":{\"id\":1,\"name\":\"Andy\"}}";

        mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

        User user = mapper.readValue(json, User.class);
        out.println(user);
    }

    // JsonParseException: Unexpected character (”’ (code 39))
    @Test
    public void testSingleQuote() throws Exception {
        String json = "{'id':1,'name':'Andy'}";

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        mapper = new ObjectMapper(factory);

        User user = mapper.readValue(json, User.class);
        out.println(user);
    }

    // JsonMappingException: Can not construct instance of
    @Test
    public void testAs() throws Exception {
        String json = "{\"animal\":{\"name\":\"lacy\"}}";

        Zoo zoo = mapper.readValue(json, Zoo.class);
        out.println(zoo);
    }

    @ToString
    private static class Zoo {
        public Animal animal;
    }

    @ToString
    @JsonDeserialize(as = Cat.class)
    private static abstract class Animal {
        public String name;
    }

    @ToString(callSuper = true)
    private static class Cat extends Animal {
        public int lives;
    }
}
