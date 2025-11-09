package com.swissre.employee.model;

/**
 * Represents the result of salary analysis for managers.
 */
public class SalaryAnalysisResult {
    private final Employee manager;
    private final double actualSalary;
    private final double averageSubordinateSalary;
    private final double minimumExpectedSalary;
    private final double maximumExpectedSalary;
    private final double difference;
    private final boolean earningTooLittle;
    private final boolean earningTooMuch;

    public SalaryAnalysisResult(Employee manager, double actualSalary,
                                double averageSubordinateSalary,
                                double minimumExpectedSalary,
                                double maximumExpectedSalary) {
        this.manager = manager;
        this.actualSalary = actualSalary;
        this.averageSubordinateSalary = averageSubordinateSalary;
        this.minimumExpectedSalary = minimumExpectedSalary;
        this.maximumExpectedSalary = maximumExpectedSalary;

        if (actualSalary < minimumExpectedSalary) {
            this.earningTooLittle = true;
            this.earningTooMuch = false;
            this.difference = minimumExpectedSalary - actualSalary;
        } else if (actualSalary > maximumExpectedSalary) {
            this.earningTooLittle = false;
            this.earningTooMuch = true;
            this.difference = actualSalary - maximumExpectedSalary;
        } else {
            this.earningTooLittle = false;
            this.earningTooMuch = false;
            this.difference = 0.0;
        }
    }

    public Employee getManager() {
        return manager;
    }

    public double getDifference() {
        return difference;
    }

    public boolean isEarningTooLittle() {
        return earningTooLittle;
    }

    public boolean isEarningTooMuch() {
        return earningTooMuch;
    }

    public boolean hasIssue() {
        return earningTooLittle || earningTooMuch;
    }
}

