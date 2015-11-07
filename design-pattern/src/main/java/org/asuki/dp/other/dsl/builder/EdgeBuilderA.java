package org.asuki.dp.other.dsl.builder;

import lombok.Getter;
import org.asuki.dp.other.dsl.Edge;
import org.asuki.dp.other.dsl.Vertex;

public class EdgeBuilderA {

    @Getter
    private Edge edge;

    private GraphBuilderA gBuilder;

    public EdgeBuilderA(GraphBuilderA gBuilder) {
        this.gBuilder = gBuilder;
        edge = new Edge();
    }

    public EdgeBuilderA from(String label) {

        Vertex vertex = new Vertex(label);
        edge.setFromVertex(vertex);
        gBuilder.getGraph().addVertex(vertex);

        return this;
    }

    public EdgeBuilderA to(String label) {

        Vertex vertex = new Vertex(label);
        edge.setToVertex(vertex);
        gBuilder.getGraph().addVertex(vertex);

        return this;
    }

    public GraphBuilderA weight(Double weight) {
        edge.setWeight(weight);
        return gBuilder;
    }

}
