package org.mql.java.examples;

import org.mql.java.annotations.Relation;

public class Department {
    private String departmentName;
    
    @Relation("Aggregation")
    private Employee employee;

    public Department(String departmentName, Employee employee) {
        this.departmentName = departmentName;
        this.employee = employee;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Employee getEmployee() {
        return employee;
    }
}
