package org.asuki.dp.other.dsl.fluent;


public interface PersonBuilder {

    public PersonBuilder firstName(String firstName);

    public PersonBuilder lastName(String lastName);

    public Person create();
}
