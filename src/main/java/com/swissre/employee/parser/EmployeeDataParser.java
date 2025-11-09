package com.swissre.employee.parser;

import com.swissre.employee.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses employee data from CSV files and builds the organizational structure.
 */
public class EmployeeDataParser {

    private static final String COMMA_DELIMITER = ",";
    private static final int ID_INDEX = 0;
    private static final int FIRST_NAME_INDEX = 1;
    private static final int LAST_NAME_INDEX = 2;
    private static final int SALARY_INDEX = 3;
    private static final int MANAGER_ID_INDEX = 4;

    /**
     * Reads employee data from a CSV file and builds the organizational hierarchy.
     *
     * @param filePath path to the CSV file
     * @return list of all employees with relationships established
     * @throws IOException if file cannot be read
     * @throws IllegalArgumentException if file format is invalid
     */
    public List<Employee> parseEmployeeData(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();
        Map<String, Employee> employeeMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                Employee employee = parseEmployeeLine(line, lineNumber);
                employees.add(employee);
                employeeMap.put(employee.getId(), employee);
            }
        }

        // Build the organizational hierarchy
        buildHierarchy(employees, employeeMap);

        return employees;
    }

    /**
     * Parses a single line from the CSV file into an Employee object.
     */
    private Employee parseEmployeeLine(String line, int lineNumber) {
        String[] values = line.split(COMMA_DELIMITER, -1);

        if (values.length < 5) {
            throw new IllegalArgumentException(
                    String.format("Invalid CSV format at line %d: expected 5 columns, found %d",
                            lineNumber, values.length));
        }

        String id = values[ID_INDEX].trim();
        String firstName = values[FIRST_NAME_INDEX].trim();
        String lastName = values[LAST_NAME_INDEX].trim();
        String salaryStr = values[SALARY_INDEX].trim();
        String managerId = values[MANAGER_ID_INDEX].trim();

        if (id.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Employee ID cannot be empty at line %d", lineNumber));
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                throw new IllegalArgumentException(
                        String.format("Salary cannot be negative at line %d", lineNumber));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid salary format at line %d: %s", lineNumber, salaryStr));
        }

        return new Employee(id, firstName, lastName, salary, managerId);
    }

    /**
     * Builds the manager-subordinate relationships in the organizational hierarchy.
     */
    private void buildHierarchy(List<Employee> employees, Map<String, Employee> employeeMap) {
        for (Employee employee : employees) {
            if (!employee.isCEO()) {
                String managerId = employee.getManagerId();
                Employee manager = employeeMap.get(managerId);

                if (manager == null) {
                    throw new IllegalArgumentException(
                            String.format("Manager with ID %s not found for employee %s",
                                    managerId, employee.getId()));
                }

                employee.setManager(manager);
                manager.addDirectReport(employee);
            }
        }
    }
}