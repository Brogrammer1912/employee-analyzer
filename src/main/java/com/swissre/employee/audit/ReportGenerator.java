package com.swissre.employee.audit;

import com.swissre.employee.model.ReportingLineResult;
import com.swissre.employee.model.SalaryAnalysisResult;

import java.util.List;

/**
 * Generates console reports for organizational analysis results.
 */
public class ReportGenerator {

    /**
     * Generates a complete report including salary compliance and reporting line analysis.
     */
    public void generateReport(List<SalaryAnalysisResult> salaryResults,
                               List<ReportingLineResult> reportingLineResults) {
        System.out.println("========================================");
        System.out.println("ORGANIZATIONAL STRUCTURE ANALYSIS REPORT");
        System.out.println("========================================");
        System.out.println();

        generateSalaryComplianceReport(salaryResults);
        System.out.println();
        generateReportingLineReport(reportingLineResults);

        System.out.println();
        System.out.println("========================================");
        System.out.println("END OF REPORT");
        System.out.println("========================================");
    }

    /**
     * Generates the salary compliance section of the report.
     */
    private void generateSalaryComplianceReport(List<SalaryAnalysisResult> results) {
        System.out.println("SALARY COMPLIANCE ANALYSIS");
        System.out.println("------------------------------------------");

        List<SalaryAnalysisResult> underEarning = results.stream()
                .filter(SalaryAnalysisResult::isEarningTooLittle)
                .toList();

        List<SalaryAnalysisResult> overEarning = results.stream()
                .filter(SalaryAnalysisResult::isEarningTooMuch)
                .toList();

        if (underEarning.isEmpty() && overEarning.isEmpty()) {
            System.out.println("✓ All managers' salaries are within acceptable range.");
        } else {
            if (!underEarning.isEmpty()) {
                System.out.println("Managers earning LESS than they should:");
                System.out.println();
                for (SalaryAnalysisResult result : underEarning) {
                    System.out.printf("  • %s%n", result.getManager().getFullName());
                    System.out.printf("    Current salary: $%.2f%n", result.getManager().getSalary());
                    System.out.printf("    Should earn at least: $%.2f%n",
                            result.getManager().getSalary() + result.getDifference());
                    System.out.printf("    Underpaid by: $%.2f%n", result.getDifference());
                    System.out.println();
                }
            }

            if (!overEarning.isEmpty()) {
                System.out.println("Managers earning MORE than they should:");
                System.out.println();
                for (SalaryAnalysisResult result : overEarning) {
                    System.out.printf("  • %s%n", result.getManager().getFullName());
                    System.out.printf("    Current salary: $%.2f%n", result.getManager().getSalary());
                    System.out.printf("    Should earn at most: $%.2f%n",
                            result.getManager().getSalary() - result.getDifference());
                    System.out.printf("    Overpaid by: $%.2f%n", result.getDifference());
                    System.out.println();
                }
            }
        }
    }

    /**
     * Generates the reporting line section of the report.
     */
    private void generateReportingLineReport(List<ReportingLineResult> results) {
        System.out.println("REPORTING LINE ANALYSIS");
        System.out.println("------------------------------------------");

        if (results.isEmpty()) {
            System.out.println("✓ All employees have acceptable reporting lines (≤ 4 managers).");
        } else {
            System.out.println("Employees with reporting lines that are TOO LONG:");
            System.out.println();
            for (ReportingLineResult result : results) {
                System.out.printf("  • %s%n", result.getEmployee().getFullName());
                System.out.printf("    Number of managers: %d%n", result.getManagerLevels());
                System.out.printf("    Exceeds limit by: %d level(s)%n", result.getExcessLevels());
                System.out.println();
            }
        }
    }
}