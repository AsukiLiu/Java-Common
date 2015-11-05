package org.asuki.tool.modelmapper;

import static org.testng.Assert.assertEquals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.testng.annotations.Test;

public class FlatteningATest {

    @Test
    public void shouldOrder2OrderDto() {
        // @formatter:off
        Order order = new Order(
                new Customer("Andy"),
                new Address("1 Main Street", "Seattle"),
                new Address("2 Main Street", "San Francisco"));
        // @formatter:on

        ModelMapper modelMapper = new ModelMapper();
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        // @formatter:off
        assertEquals(
                dto.getCustomerName(),
                order.getCustomer().getName());
        assertEquals(
                dto.getShippingStreetAddress(),
                order.getShippingAddress().getStreet());
        assertEquals(
                dto.getShippingCity(),
                order.getShippingAddress().getCity());
        assertEquals(
                dto.getBillingStreetAddress(),
                order.getBillingAddress().getStreet());
        assertEquals(
                dto.getBillingCity(),
                order.getBillingAddress().getCity());
        // @formatter:on
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Address {
        private String street;
        private String city;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Customer {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Order {
        private Customer customer;
        private Address billingAddress;
        private Address shippingAddress;
    }

    @Getter
    @Setter
    static class OrderDTO {
        private String customerName;
        private String shippingStreetAddress;
        private String shippingCity;
        private String billingStreetAddress;
        private String billingCity;
    }

}
