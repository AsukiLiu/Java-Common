package org.asuki.dp.other.dsl.builder;

import org.asuki.dp.other.dsl.Edge;
import org.asuki.dp.other.dsl.Vertex;

public class EdgeBuilderB {

    public static Edge edge(Vertex from, Vertex to, Double weight) {

        return new Edge(from, to, weight);
    }

    public static Double weight(Double weight) {
        return weight;
    }

}
