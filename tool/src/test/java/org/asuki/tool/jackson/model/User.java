package org.asuki.tool.jackson.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.asuki.tool.jackson.Jackson2ViewTest.Views;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "user")
public class User {
    private int id;

    @JsonView(Views.Public.class)
    private String name;
}
