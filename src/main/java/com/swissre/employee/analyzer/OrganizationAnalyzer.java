package com.swissre.employee.analyzer;

import com.swissre.employee.model.Employee;
import com.swissre.employee.model.ReportingLineResult;
import com.swissre.employee.model.SalaryAnalysisResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes organizational structure for salary compliance and reporting line issues.
 */
public class OrganizationAnalyzer {

    private static final double MIN_SALARY_RATIO = 1.20; // 20% more than average
    private static final double MAX_SALARY_RATIO = 1.50; // 50% more than average
    private static final int MAX_MANAGER_LEVELS = 4;

    /**
     * Analyzes manager salaries to ensure they fall within the acceptable range.
     *
     * @param employees list of all employees
     * @return list of salary analysis results for managers with issues
     */
    public List<SalaryAnalysisResult> analyzeSalaryCompliance(List<Employee> employees) {
        List<SalaryAnalysisResult> results = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.isManager()) {
                SalaryAnalysisResult result = analyzeManagerSalary(employee);
                if (result.hasIssue()) {
                    results.add(result);
                }
            }
        }

        return results;
    }

    /**
     * Analyzes a single manager's salary against their subordinates' average.
     */
    private SalaryAnalysisResult analyzeManagerSalary(Employee manager) {
        double averageSubordinateSalary = manager.getAverageDirectReportSalary();
        double actualSalary = manager.getSalary();
        double minimumExpectedSalary = averageSubordinateSalary * MIN_SALARY_RATIO;
        double maximumExpectedSalary = averageSubordinateSalary * MAX_SALARY_RATIO;

        return new SalaryAnalysisResult(
                manager,
                actualSalary,
                averageSubordinateSalary,
                minimumExpectedSalary,
                maximumExpectedSalary
        );
    }

    /**
     * Identifies employees with reporting lines that are too long.
     *
     * @param employees list of all employees
     * @return list of employees with excessive reporting lines
     */
    public List<ReportingLineResult> analyzeReportingLines(List<Employee> employees) {
        List<ReportingLineResult> results = new ArrayList<>();

        for (Employee employee : employees) {
            int managerLevels = employee.getManagerLevels();
            if (managerLevels > MAX_MANAGER_LEVELS) {
                results.add(new ReportingLineResult(
                        employee,
                        managerLevels,
                        MAX_MANAGER_LEVELS
                ));
            }
        }

        return results;
    }

    /**
     * Gets the minimum salary ratio (20% more than average).
     */
    public double getMinSalaryRatio() {
        return MIN_SALARY_RATIO;
    }

    /**
     * Gets the maximum salary ratio (50% more than average).
     */
    public double getMaxSalaryRatio() {
        return MAX_SALARY_RATIO;
    }

    /**
     * Gets the maximum allowed manager levels (4).
     */
    public int getMaxManagerLevels() {
        return MAX_MANAGER_LEVELS;
    }
}