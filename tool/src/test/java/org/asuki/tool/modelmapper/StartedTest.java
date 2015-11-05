package org.asuki.tool.modelmapper;

import static org.testng.Assert.assertEquals;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

public class StartedTest {

    @Test
    public void shouldMapImplicitly() {
        Order order = createOrder();

        ModelMapper modelMapper = new ModelMapper();
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        assertOrdersEqual(order, dto);
    }

    @Test
    public void shouldMapExplicitly() {
        Order order = createOrder();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
            @Override
            protected void configure() {
                map().setBillingStreet(source.getBillingAddress().getStreet());
                map(source.getBillingAddress().getCity(),
                        destination.getBillingCity());
            }
        });

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        assertOrdersEqual(order, dto);
    }

    private static Order createOrder() {
        return new Order(new Customer(new Name("Joe", "Smith")), new Address(
                "1 Main Street", "Seattle"));
    }

    private static void assertOrdersEqual(Order order, OrderDTO dto) {
        // @formatter:off
        assertEquals(
                dto.getCustomerFirstName(), 
                order.getCustomer().getName().getFirstName());
        assertEquals(
                dto.getCustomerLastName(), 
                order.getCustomer().getName().getLastName());
        assertEquals(
                dto.getBillingStreet(), 
                order.getBillingAddress().getStreet());
        assertEquals(
                dto.getBillingCity(), 
                order.getBillingAddress().getCity());
        // @formatter:off
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
    static class Name {
        private String firstName;
        private String lastName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Customer {
        private Name name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Order {
        private Customer customer;
        private Address billingAddress;
    }

    @Getter
    @Setter
    static class OrderDTO {
        private String customerFirstName;
        private String customerLastName;
        private String billingStreet;
        private String billingCity;
    }
}
