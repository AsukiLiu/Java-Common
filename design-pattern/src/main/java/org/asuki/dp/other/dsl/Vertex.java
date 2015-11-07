package org.asuki.dp.other.dsl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Vertex implements Comparable<Vertex> {

    @Getter
    @Setter
    private String label;

    public Vertex(String label) {
        this.label = label.toUpperCase();
    }

    @Override
    public int compareTo(Vertex vertex) {
        return getLabel().compareTo(vertex.getLabel());
    }

}
