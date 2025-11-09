package com.swissre.employee;

import com.swissre.employee.analyzer.OrganizationAnalyzer;
import com.swissre.employee.model.Employee;
import com.swissre.employee.model.ReportingLineResult;
import com.swissre.employee.model.SalaryAnalysisResult;
import com.swissre.employee.parser.EmployeeDataParser;
import com.swissre.employee.audit.ReportGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Main application class for analyzing employee organizational structure.
 *
 * This application reads employee data from a CSV file and performs two main analyses:
 * 1. Salary Compliance: Ensures managers earn 20-50% more than their direct reports' average
 * 2. Reporting Line Length: Identifies employees with more than 4 managers between them and the CEO
 */
public class EmployeeAnalyzerApplication {

    private final EmployeeDataParser parser;
    private final OrganizationAnalyzer analyzer;
    private final ReportGenerator reporter;

    public EmployeeAnalyzerApplication() {
        this.parser = new EmployeeDataParser();
        this.analyzer = new OrganizationAnalyzer();
        this.reporter = new ReportGenerator();
    }

    /**
     * Analyzes employee data from the specified file and generates a report.
     *
     * @param filePath path to the CSV file containing employee data
     */
    public void analyze(String filePath) {
        try {
            // Parse employee data
            List<Employee> employees = parser.parseEmployeeData(filePath);

            if (employees.isEmpty()) {
                System.out.println("No employee data found in file: " + filePath);
                return;
            }

            // Perform analysis
            List<SalaryAnalysisResult> salaryResults = analyzer.analyzeSalaryCompliance(employees);
            List<ReportingLineResult> reportingLineResults = analyzer.analyzeReportingLines(employees);

            // Generate report
            reporter.generateReport(salaryResults, reportingLineResults);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing employee data: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments, expects one argument: path to CSV file
     */
    public static void main(String[] args) {
        String filePath;

        if (args.length != 1) {
            System.out.println("===========================================");
            System.out.println("No CSV file specified. Using sample data (employees-extended.csv).");
            System.out.println("===========================================");
            System.out.println();
            System.out.println("Usage: java -jar employee-analyzer.jar <path-to-csv-file>");
            System.out.println("Example: java -jar employee-analyzer.jar employees.csv");
            System.out.println();

            // Use sample CSV file for demonstration
            filePath = "employees.csv";
        } else {
            filePath = args[0];
        }

        EmployeeAnalyzerApplication app = new EmployeeAnalyzerApplication();
        app.analyze(filePath);
    }
}