package org.mql.java.examples;

public class Employee extends Person {
    private String jobTitle;
    private double salary;
    private final Gender gender;

    public Employee(String name, int age, String jobTitle, double salary, Gender gender) {
        super(name, age);
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.gender = gender;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public double getSalary() {
        return salary;
    }
}
