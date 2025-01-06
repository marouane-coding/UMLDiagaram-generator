package org.mql.java.examples;

public class PersonWithAddress extends Person implements Alien {
    private Address address;
    private Gender gender;

    public PersonWithAddress(String name, int age, Address address) {
        super(name, age);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
