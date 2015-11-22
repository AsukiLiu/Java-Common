package org.asuki.tool.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.asuki.tool.jackson.model.Item;
import org.asuki.tool.jackson.model.MyDto;
import org.asuki.tool.jackson.model.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Jackson2MarshallingTest {

    private ObjectMapper mapper;

    @BeforeMethod
    public void init() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testIgnore() throws Exception {
        mapper
                .setSerializationInclusion(Include.NON_NULL)
                .setSerializationInclusion(Include.NON_EMPTY);

        mapper.addMixInAnnotations(String.class, MyMixIn.class);

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("myFilter", SimpleBeanPropertyFilter.serializeAllExcept("intValue"));

        MyDto dto = new MyDto();
        dto.setStringValue("");

        String dtoAsString = mapper.writer(filters).writeValueAsString(dto);
        out.println(dtoAsString);

        assertThat(dtoAsString, not(containsString("intValue")));
        assertThat(dtoAsString, containsString("bVal"));
        assertThat(dtoAsString, not(containsString("stringValue")));
    }

    @JsonIgnoreType
    private static class MyMixIn {
    }

//    @Test
    public void testCustomFilter() throws Exception {

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("myFilter", new CustomPropertyFilter());

        MyDto dto = new MyDto();
        dto.setIntValue(-1);

        String dtoAsString = mapper.writer(filters).writeValueAsString(dto);
        out.println(dtoAsString);

        assertThat(dtoAsString, not(containsString("intValue")));
    }

    private static class CustomPropertyFilter extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
                throws Exception {

            if (include(writer)) {
                if (!writer.getName().equals("intValue")) {
                    writer.serializeAsField(pojo, jgen, provider);
                    return;
                }

                int intValue = ((MyDto) pojo).getIntValue();
                if (intValue >= 0) {
                    writer.serializeAsField(pojo, jgen, provider);
                }
            } else if (!jgen.canOmitFields()) {
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        }
    }

    @Test
    public void testEnum() throws Exception {
        String enumAsString = mapper.writeValueAsString(Type.TYPE1);
        out.println(enumAsString);
    }


    //    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @JsonSerialize(using = TypeSerializer.class)
    private enum Type {
        TYPE1(1, "Type A"), TYPE2(2, "Type B");

        @Getter
        private Integer id;
        private String name;

        //        @JsonValue
        public String getName() {
            return name;
        }
    }

    private static class TypeSerializer extends JsonSerializer<Type> {
        @Override
        public void serialize(Type value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeFieldName("id");
            jgen.writeNumber(value.getId());
            jgen.writeFieldName("name");
            jgen.writeString(value.getName());
            jgen.writeEndObject();
        }
    }

    @Test//(expectedExceptions = JsonMappingException.class)
    public void testNoAccessors() throws Exception {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String dtoAsString = mapper.writeValueAsString(new MyDtoNoAccessors());

        assertThat(dtoAsString, containsString("intValue"));
        assertThat(dtoAsString, containsString("stringValue"));
        assertThat(dtoAsString, containsString("booleanValue"));
    }

    //    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    private static class MyDtoNoAccessors {
        String stringValue;
        int intValue;
        boolean booleanValue;
    }

    @Test
    public void testCustomSerializer() throws Exception {

        Item item = new Item(1, "ItemA", new User(2, "UserA"));

        SimpleModule module = new SimpleModule();
        module.addSerializer(Item.class, new ItemSerializer());
        mapper.registerModule(module);

        String itemAsString = mapper.writeValueAsString(item);
        out.println(itemAsString);
    }

    public static class ItemSerializer extends JsonSerializer<Item> {
        @Override
        public void serialize(Item value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeNumberField("id", value.getId());
            jgen.writeStringField("itemName", value.getItemName());
            jgen.writeNumberField("owner", value.getOwner().getId());
            jgen.writeEndObject();
        }
    }

    @Test
    public void testCustomSerializerFactory() throws Exception {
        User user = new User(1, "Andy");

        SerializerFactory serializerFactory = BeanSerializerFactory.instance
                .withSerializerModifier(new CustomBeanSerializerModifier());

        mapper.setSerializerFactory(serializerFactory);

        String userAsString = mapper.writeValueAsString(user);
        out.println(userAsString);

        assertThat(userAsString, containsString("ANDY"));
    }

    private static class CustomBeanSerializerModifier extends BeanSerializerModifier {

        @Override
        public List<BeanPropertyWriter> changeProperties(
                SerializationConfig config, BeanDescription beanDesc,
                List<BeanPropertyWriter> beanProperties) {

            for (int i = 0, size = beanProperties.size(); i < size; i++) {
                BeanPropertyWriter writer = beanProperties.get(i);
                if (writer.getName().equals("name")) {
                    beanProperties.set(i, new UpperCasingWriter(writer));
                    break;
                }
            }

            return beanProperties;
        }
    }

    private static class UpperCasingWriter extends BeanPropertyWriter {

        public UpperCasingWriter(BeanPropertyWriter writer) {
            super(writer);
        }

        @Override
        public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
                throws Exception {

            String name = ((User) bean).getName();
            gen.writeStringField("name", (name == null) ? "" : name.toUpperCase());
        }
    }
}
