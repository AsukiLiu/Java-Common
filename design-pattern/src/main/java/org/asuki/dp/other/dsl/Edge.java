package org.asuki.dp.other.dsl;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Edge {

    private Vertex fromVertex;

    private Vertex toVertex;

    private Double weight;

}
