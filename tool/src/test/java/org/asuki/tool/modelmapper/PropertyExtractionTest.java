package org.asuki.tool.modelmapper;

import static java.util.UUID.randomUUID;
import static org.modelmapper.config.Configuration.AccessLevel.PACKAGE_PRIVATE;
import static org.testng.Assert.assertEquals;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

public class PropertyExtractionTest {

    @Test
    public void test() {

        Order order = new Order();
        order.id = randomUUID().toString();
        DeliveryAddress da1 = new DeliveryAddress();
        da1.addressId = 123;
        DeliveryAddress da2 = new DeliveryAddress();
        da2.addressId = 456;
        order.deliveryAddress = new DeliveryAddress[] { da1, da2 };

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PACKAGE_PRIVATE);

        modelMapper.createTypeMap(Order.class, OrderDTO.class)
                .setPostConverter(new Converter<Order, OrderDTO>() {
                    @Override
                    public OrderDTO convert(
                            MappingContext<Order, OrderDTO> context) {

                        DeliveryAddress[] deliveryAddress = context.getSource().deliveryAddress;

                        OrderDTO orderDto = context.getDestination();

                        orderDto.deliveryAddressId = new Integer[deliveryAddress.length];

                        for (int i = 0, len = deliveryAddress.length; i < len; i++) {
                            orderDto.deliveryAddressId[i] = deliveryAddress[i].addressId;
                        }

                        return orderDto;
                    }
                });

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        assertEquals(dto.id, order.id);
        assertEquals(dto.deliveryAddressId, new Integer[] { da1.addressId,
                da2.addressId });
    }

    static class Order {
        String id;
        DeliveryAddress[] deliveryAddress;
    }

    static class DeliveryAddress {
        Integer addressId;
    }

    static class OrderDTO {
        String id;
        Integer[] deliveryAddressId;
    }
}
