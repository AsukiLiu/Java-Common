package org.asuki.common.model;

import static org.asuki.common.util.Sample.Level.HIGH;

import org.asuki.common.annotation.Hint;
import org.asuki.common.util.Sample;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
// @Hints({ @Hint("hint1"), @Hint("hint2") }) //Before Java SE8
@Hint("hint1")
@Hint("hint2")
public class Person {

    @Sample(item = { "first name", "last name" })
    private String name;

    @Sample("30")
    private int age;

    public Person(String name) {
        this.name = name;
    }

    @Sample(item = { "item1", "item2" }, level = HIGH)
    public void doSomething() {
        // do something
    }

}
