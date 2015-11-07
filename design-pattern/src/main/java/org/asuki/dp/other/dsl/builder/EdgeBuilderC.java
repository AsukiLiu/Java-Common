package org.asuki.dp.other.dsl.builder;

import org.asuki.dp.other.dsl.Edge;
import org.asuki.dp.other.dsl.Vertex;

public class EdgeBuilderC {

    private Edge edge;

    public EdgeBuilderC() {
        edge = new Edge();
    }

    public Edge edge() {
        return edge;
    }

    public void from(String label) {
        edge.setFromVertex(new Vertex(label));
    }

    public void to(String label) {
        edge.setToVertex(new Vertex(label));
    }

    public void weight(Double weight) {
        edge.setWeight(weight);
    }
}