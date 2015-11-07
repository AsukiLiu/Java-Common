package org.asuki.dp.other.dsl.builder;

import org.asuki.dp.other.dsl.Edge;
import org.asuki.dp.other.dsl.Graph;

public class GraphBuilderB {

    public static Graph Graph(Edge... edges) {

        Graph graph = new Graph();
        for (Edge edge : edges) {
            graph.addEdge(edge);
            graph.addVertex(edge.getFromVertex());
            graph.addVertex(edge.getToVertex());
        }

        return graph;
    }

}
