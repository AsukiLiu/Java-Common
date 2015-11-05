package org.asuki.tool.modelmapper;

import static org.modelmapper.convention.MatchingStrategies.LOOSE;
import static org.testng.Assert.assertEquals;
import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FlatteningBTest {

    @Test(dataProvider = "mappers")
    public void shouldPerson2PersonDto(ModelMapper modelMapper)
            throws Exception {

        Address address = new Address();
        address.setStreet("1 Main street");
        address.setCity("San Francisco");

        Person person = new Person();
        person.setAddress(address);

        PersonDTO dto = modelMapper.map(person, PersonDTO.class);

        assertEquals(dto.getStreet(), person.getAddress().getStreet());
        assertEquals(dto.getCity(), person.getAddress().getCity());
    }

    @Getter
    @Setter
    static class Address {
        private String street;
        private String city;
    }

    @Getter
    @Setter
    static class Person {
        private Address address;
    }

    @Getter
    @Setter
    static class PersonDTO {
        private String city;
        private String street;
    }

    @DataProvider(name = "mappers")
    private Object[][] mappers() {
        return new Object[][] { { createOption1() }, { createOption2() } };
    }

    private ModelMapper createOption1() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Person, PersonDTO>() {
            @Override
            protected void configure() {
                map().setStreet(source.getAddress().getStreet());
                map(source.getAddress().getCity(), destination.getCity());
            }
        });

        return modelMapper;
    }

    private ModelMapper createOption2() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(LOOSE);
        return modelMapper;
    }

}
