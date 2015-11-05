package org.asuki.common;

import org.asuki.common.model.Person;

interface PersonFactory<P extends Person> {
    P create(String name);
}
