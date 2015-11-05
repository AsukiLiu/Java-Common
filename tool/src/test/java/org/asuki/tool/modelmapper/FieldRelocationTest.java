package org.asuki.tool.modelmapper;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

public class FieldRelocationTest {

    @Test
    public void test() throws Exception {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(new Converter<Car, AnotherCar>() {
            @Override
            public AnotherCar convert(MappingContext<Car, AnotherCar> context) {
                Person person = (Person) context.getParent().getParent()
                        .getSource();
                Car car = context.getSource();

                AnotherCar anotherCar = context.getDestination();

                anotherCar.setPersonName(person.getName());
                anotherCar.setType(car.getType());
                return anotherCar;
            }
        });

        modelMapper.addMappings(new PropertyMap<Person, AnotherPerson>() {
            @Override
            protected void configure() {
                map(source.getCars()).setCars(null);
            }
        });

        Person person = new Person();
        person.name = "Andy";
        Car car1 = new Car();
        car1.type = "Honda";
        Car car2 = new Car();
        car2.type = "Toyota";
        person.cars = asList(car1, car2);

        AnotherPerson anotherPerson = modelMapper.map(person,
                AnotherPerson.class);

        assertEquals(anotherPerson.getCars().get(0).personName, person.name);
        assertEquals(anotherPerson.getCars().get(0).type, car1.type);
        assertEquals(anotherPerson.getCars().get(1).personName, person.name);
        assertEquals(anotherPerson.getCars().get(1).type, car2.type);
    }

    @Getter
    @Setter
    static class Person {
        private String name;
        private List<Car> cars;
    }

    @Getter
    @Setter
    static class Car {
        private String type;
    }

    @Getter
    @Setter
    static class AnotherPerson {
        private List<AnotherCar> cars;
    }

    @Getter
    @Setter
    static class AnotherCar {
        private String personName;
        private String type;
    }
}
