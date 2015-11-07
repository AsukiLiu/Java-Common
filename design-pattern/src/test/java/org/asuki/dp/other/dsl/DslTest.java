package org.asuki.dp.other.dsl;

import org.asuki.dp.other.dsl.builder.GraphBuilderA;
import org.asuki.dp.other.dsl.builder.GraphBuilderB;
import org.asuki.dp.other.dsl.builder.GraphBuilderC;
import org.asuki.dp.other.dsl.fluent.Person;
import org.testng.annotations.Test;

import static org.asuki.dp.other.dsl.builder.EdgeBuilderB.edge;
import static org.asuki.dp.other.dsl.builder.EdgeBuilderB.weight;
import static org.asuki.dp.other.dsl.builder.VertexBuilderB.from;
import static org.asuki.dp.other.dsl.builder.VertexBuilderB.to;
import static org.asuki.dp.other.dsl.fluent.Person.with;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DslTest {

    // @formatter:off
    /*
    Graph(
            edges=[
                Edge(fromVertex=Vertex(label=A), toVertex=Vertex(label=B), weight=10.1), 
                Edge(fromVertex=Vertex(label=B), toVertex=Vertex(label=C), weight=20.2)
            ], 
            vertices=[
                Vertex(label=A), 
                Vertex(label=B), 
                Vertex(label=C)
            ]
        )
    */
    // @formatter:on

    @Test
    public void testDsl() {

        // By Method Chaining
        // @formatter:off
        Graph graphA = GraphBuilderA.Graph()
            .edge()
                .from("a")
                .to("b")
                .weight(10.1)
            .edge()
                .from("b")
                .to("c")
                .weight(20.2)
            .getGraph();
        // @formatter:on

        // By Nested Functions
        Graph graphB = GraphBuilderB.Graph(
                edge(from("a"), to("b"), weight(10.1)),
                edge(from("b"), to("c"), weight(20.2)));

        // By Lambda Expression
        Graph graphC = GraphBuilderC.Graph(g -> {
            g.edge(e -> {
                e.from("a");
                e.to("b");
                e.weight(10.1);
            });
            g.edge(e -> {
                e.from("b");
                e.to("c");
                e.weight(20.2);
            });
        });

        assertThat(graphA.toString(), is(graphB.toString()));
        assertThat(graphB.toString(), is(graphC.toString()));
    }

    @Test
    public void testFluentBuilder() {

        Person person1 = new Person();
        person1.setFirstName("Asuki");
        person1.setLastName("Liu");

        Person person2 = with().firstName("Asuki").lastName("Liu").create();

        assertThat(person1.toString(), is(person2.toString()));
    }

}
