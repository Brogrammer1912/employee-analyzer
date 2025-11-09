package com.swissre.employee.model;

/**
 * Represents an employee with a reporting line that is too long.
 */
public class ReportingLineResult {
    private final Employee employee;
    private final int managerLevels;
    private final int excessLevels;

    public ReportingLineResult(Employee employee, int managerLevels, int maxAllowedLevels) {
        this.employee = employee;
        this.managerLevels = managerLevels;
        this.excessLevels = managerLevels - maxAllowedLevels;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getManagerLevels() {
        return managerLevels;
    }

    public int getExcessLevels() {
        return excessLevels;
    }
}
