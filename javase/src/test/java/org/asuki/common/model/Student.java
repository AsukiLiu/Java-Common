package org.asuki.common.model;

import com.google.common.base.Objects;

public class Student extends Person {
    public Student(Person person) {
        super.setAge(person.getAge());
        super.setName(person.getName());
    }

    @Override
    public String toString(){
        return Objects.toStringHelper(this).omitNullValues()
            .add("name", super.getName())
            .add("age", super.getAge())
            .toString();
    }
}
