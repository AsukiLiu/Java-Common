package org.asuki.tool.jackson.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "user")
public class User {
    private int id;

    private String name;
}
