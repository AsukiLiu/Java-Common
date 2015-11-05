package org.asuki.common;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.asuki.common.model.Person;
import org.asuki.common.util.GenericValidator;

public class PersonValidator extends GenericValidator<Person> {

    private static final List<Predicate<Person>> VALIDATORS = new LinkedList<>();

    static {
        VALIDATORS.add(person -> !isNullOrEmpty(person.getName()));
        VALIDATORS.add(person -> person.getAge() > 0);
        // more...
    }

    public PersonValidator() {
        super(VALIDATORS);
    }
}
