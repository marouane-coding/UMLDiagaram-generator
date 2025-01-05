package org.mql.java.examples;

import org.mql.java.annotations.Relation;

public class PersonWithAddress extends Person implements Alien {
    @Relation("Composition")
    private Address address;

    public PersonWithAddress(String name, int age, Address address) {
        super(name, age);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
