package org.mql.java.examples;

public class PersonWithAddress extends Person implements Alien {
    private Address address;
    private final Gender gender;

    public PersonWithAddress(String name, int age, Address address, Gender gender) {
        super(name, age);
        this.address = address;
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }
}
