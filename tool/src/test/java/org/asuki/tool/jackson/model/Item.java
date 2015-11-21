package org.asuki.tool.jackson.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.asuki.tool.jackson.Jackson2MarshallingTest.ItemSerializer;
//import org.asuki.tool.jackson.Jackson2UnmarshallingTest.ItemDeserializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//@JsonDeserialize(using = ItemDeserializer.class)
@JsonSerialize(using = ItemSerializer.class)
public class Item {
    private int id;
    private String itemName;
    private User owner;
}
