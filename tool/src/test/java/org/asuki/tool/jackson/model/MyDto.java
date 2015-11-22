package org.asuki.tool.jackson.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
//@JsonFilter("myFilter")
//@JsonInclude(Include.NON_NULL)
//@JsonIgnoreProperties(value = { "intValue" })
//@JsonIgnoreProperties(ignoreUnknown = true)
public class MyDto {
    //@JsonInclude(Include.NON_EMPTY)
    private String stringValue;

    //@JsonIgnore
    private int intValue;

    @JsonProperty("bVal")
    private boolean booleanValue;
}
