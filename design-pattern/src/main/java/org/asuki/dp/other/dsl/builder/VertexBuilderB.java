package org.asuki.dp.other.dsl.builder;

import org.asuki.dp.other.dsl.Vertex;

public class VertexBuilderB {

    public static Vertex from(String label) {
        return new Vertex(label);
    }

    public static Vertex to(String label) {
        return new Vertex(label);
    }

}
