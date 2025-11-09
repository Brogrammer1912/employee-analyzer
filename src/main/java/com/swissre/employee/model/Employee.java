package com.swissre.employee.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee in the organizational structure.
 */
public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final String managerId;

    private Employee manager;
    private List<Employee> directReports;

    public Employee(String id, String firstName, String lastName, double salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
        this.directReports = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getSalary() {
        return salary;
    }

    public String getManagerId() {
        return managerId;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void addDirectReport(Employee employee) {
        this.directReports.add(employee);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isManager() {
        return !directReports.isEmpty();
    }

    public boolean isCEO() {
        return managerId == null || managerId.trim().isEmpty();
    }

    /**
     * Calculates the average salary of direct subordinates.
     */
    public double getAverageDirectReportSalary() {
        if (directReports.isEmpty()) {
            return 0.0;
        }
        return directReports.stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
    }

    /**
     * Counts the number of managers between this employee and the CEO.
     */
    public int getManagerLevels() {
        int levels = 0;
        Employee current = this.manager;
        while (current != null) {
            levels++;
            current = current.getManager();
        }
        return levels;
    }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %s)", firstName, lastName, id);
    }
}