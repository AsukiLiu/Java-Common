package org.asuki.tool.modelmapper;

import static org.modelmapper.convention.MatchingStrategies.LOOSE;
import static org.testng.Assert.assertEquals;
import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProjectionBTest {

    @Test(dataProvider = "mappers")
    public void shouldOrderDto2Order(ModelMapper modelMapper) {

        OrderDTO dto = new OrderDTO();
        dto.setStreet("1 Main Street");
        dto.setCity("Seattle");

        Order order = modelMapper.map(dto, Order.class);

        assertEquals(order.getAddress().getStreet(), dto.getStreet());
        assertEquals(order.getAddress().getCity(), dto.getCity());
    }

    @Getter
    @Setter
    static class Address {
        private String street;
        private String city;
    }

    @Getter
    @Setter
    static class Order {
        private Address address;
    }

    @Getter
    @Setter
    static class OrderDTO {
        private String street;
        private String city;
    }

    @DataProvider(name = "mappers")
    private Object[][] mappers() {
        return new Object[][] { { createOption1() }, { createOption2() } };
    }

    private ModelMapper createOption1() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<OrderDTO, Order>() {
            @Override
            protected void configure() {
                map().getAddress().setStreet(source.getStreet());
                map().getAddress().setCity(source.getCity());
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
