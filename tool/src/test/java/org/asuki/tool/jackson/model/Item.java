package org.asuki.tool.jackson.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.asuki.tool.jackson.Jackson2ViewTest.Views;
import org.asuki.tool.jackson.Jackson2UnmarshallingTest.ItemDeserializer;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ItemDeserializer.class)
//@JsonSerialize(using = ItemSerializer.class)
public class Item {
    @JsonView(Views.Public.class)
    private int id;

    @JsonView(Views.Public.class)
    private String itemName;

    @JsonView(Views.Internal.class)
    private User owner;
}
