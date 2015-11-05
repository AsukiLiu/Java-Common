package org.asuki.tool.modelmapper;

import static org.testng.Assert.assertEquals;

import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.testng.annotations.Test;

public class ProjectionATest {

    @Test
    public void shouldOrderDto2Order() {

        OrderDto dto = new OrderDto();
        dto.setCustomerName("Joe Smith");
        dto.setStreetAddress("1 Main Street");

        ModelMapper modelMapper = new ModelMapper();
        Order order = modelMapper.map(dto, Order.class);

        // @formatter:off
        assertEquals(
                order.getCustomer().getName(),
                dto.getCustomerName());
        assertEquals(
                order.getAddress().getStreet(),
                dto.getStreetAddress());
        // @formatter:on
    }

    @Getter
    @Setter
    static class Address {
        private String street;
    }

    @Getter
    @Setter
    static class Customer {
        private String name;
    }

    @Getter
    @Setter
    static class Order {
        private Customer customer;
        private Address address;
    }

    @Getter
    @Setter
    static class OrderDto {
        private String customerName;
        private String streetAddress;
    }
}
