package com.swissre.employee.analyzer;

import com.swissre.employee.model.Employee;
import com.swissre.employee.model.ReportingLineResult;
import com.swissre.employee.model.SalaryAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationAnalyzerTest {

    private OrganizationAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new OrganizationAnalyzer();
    }

    @Test
    void testManagerEarningTooLittle() {
        List<Employee> employees = createTestOrganization();

        // CEO earns 60000, subordinates average 46000
        // Should earn at least 46000 * 1.2 = 55200
        // But earns 60000, so within range

        // Manager1 earns 45000, subordinate earns 50000
        // Should earn at least 50000 * 1.2 = 60000
        // But earns only 45000, so underpaid by 15000

        List<SalaryAnalysisResult> results = analyzer.analyzeSalaryCompliance(employees);

        assertFalse(results.isEmpty());
        SalaryAnalysisResult manager1Result = results.stream()
                .filter(r -> r.getManager().getId().equals("124"))
                .findFirst()
                .orElse(null);

        assertNotNull(manager1Result);
        assertTrue(manager1Result.isEarningTooLittle());
        assertEquals(15000, manager1Result.getDifference(), 0.01);
    }

    @Test
    void testManagerEarningTooMuch() {
        Employee ceo = new Employee("1", "CEO", "Person", 100000, null);
        Employee subordinate = new Employee("2", "Sub", "Person", 40000, "1");

        ceo.addDirectReport(subordinate);
        subordinate.setManager(ceo);

        // CEO earns 100000, subordinate earns 40000
        // Should earn at most 40000 * 1.5 = 60000
        // But earns 100000, so overpaid by 40000

        List<Employee> employees = List.of(ceo, subordinate);
        List<SalaryAnalysisResult> results = analyzer.analyzeSalaryCompliance(employees);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isEarningTooMuch());
        assertEquals(40000, results.get(0).getDifference(), 0.01);
    }

    @Test
    void testManagerWithinSalaryRange() {
        Employee manager = new Employee("1", "Manager", "Person", 60000, null);
        Employee sub1 = new Employee("2", "Sub", "One", 45000, "1");
        Employee sub2 = new Employee("3", "Sub", "Two", 47000, "1");

        manager.addDirectReport(sub1);
        manager.addDirectReport(sub2);
        sub1.setManager(manager);
        sub2.setManager(manager);

        // Manager earns 60000, subordinates average 46000
        // Range: 46000 * 1.2 = 55200 to 46000 * 1.5 = 69000
        // 60000 is within range

        List<Employee> employees = List.of(manager, sub1, sub2);
        List<SalaryAnalysisResult> results = analyzer.analyzeSalaryCompliance(employees);

        assertTrue(results.isEmpty());
    }

    @Test
    void testReportingLineTooLong() {
        // Create a hierarchy with 5 levels
        Employee ceo = new Employee("1", "CEO", "Person", 100000, null);
        Employee level1 = new Employee("2", "L1", "Manager", 80000, "1");
        Employee level2 = new Employee("3", "L2", "Manager", 70000, "2");
        Employee level3 = new Employee("4", "L3", "Manager", 60000, "3");
        Employee level4 = new Employee("5", "L4", "Manager", 55000, "4");
        Employee level5 = new Employee("6", "L5", "Employee", 50000, "5");

        buildChain(ceo, level1, level2, level3, level4, level5);

        List<Employee> employees = List.of(ceo, level1, level2, level3, level4, level5);
        List<ReportingLineResult> results = analyzer.analyzeReportingLines(employees);

        // level5 has 5 managers (exceeds limit of 4 by 1)
        assertEquals(1, results.size());
        assertEquals("6", results.get(0).getEmployee().getId());
        assertEquals(5, results.get(0).getManagerLevels());
        assertEquals(1, results.get(0).getExcessLevels());
    }

    @Test
    void testReportingLineWithinLimit() {
        // Create a hierarchy with 4 levels (at the limit)
        Employee ceo = new Employee("1", "CEO", "Person", 100000, null);
        Employee level1 = new Employee("2", "L1", "Manager", 80000, "1");
        Employee level2 = new Employee("3", "L2", "Manager", 70000, "2");
        Employee level3 = new Employee("4", "L3", "Manager", 60000, "3");
        Employee level4 = new Employee("5", "L4", "Employee", 50000, "4");

        buildChain(ceo, level1, level2, level3, level4);

        List<Employee> employees = List.of(ceo, level1, level2, level3, level4);
        List<ReportingLineResult> results = analyzer.analyzeReportingLines(employees);

        // level4 has exactly 4 managers (at the limit, should pass)
        assertTrue(results.isEmpty());
    }

    @Test
    void testMultipleEmployeesWithLongReportingLines() {
        Employee ceo = new Employee("1", "CEO", "Person", 100000, null);

        // Branch 1 - too long (5 levels)
        Employee b1l1 = new Employee("2", "B1L1", "Manager", 80000, "1");
        Employee b1l2 = new Employee("3", "B1L2", "Manager", 70000, "2");
        Employee b1l3 = new Employee("4", "B1L3", "Manager", 60000, "3");
        Employee b1l4 = new Employee("5", "B1L4", "Manager", 55000, "4");
        Employee b1l5 = new Employee("6", "B1L5", "Employee", 50000, "5");

        buildChain(ceo, b1l1, b1l2, b1l3, b1l4, b1l5);

        // Branch 2 - also too long (5 levels)
        Employee b2l1 = new Employee("7", "B2L1", "Manager", 80000, "1");
        Employee b2l2 = new Employee("8", "B2L2", "Manager", 70000, "7");
        Employee b2l3 = new Employee("9", "B2L3", "Manager", 60000, "8");
        Employee b2l4 = new Employee("10", "B2L4", "Manager", 55000, "9");
        Employee b2l5 = new Employee("11", "B2L5", "Employee", 52000, "10");

        buildChain(ceo, b2l1, b2l2, b2l3, b2l4, b2l5);

        List<Employee> employees = List.of(ceo, b1l1, b1l2, b1l3, b1l4, b1l5,
                b2l1, b2l2, b2l3, b2l4, b2l5);
        List<ReportingLineResult> results = analyzer.analyzeReportingLines(employees);

        assertEquals(2, results.size());
    }

    private List<Employee> createTestOrganization() {
        // Create the sample organization from the requirements
        Employee ceo = new Employee("123", "Joe", "Doe", 60000, null);
        Employee manager1 = new Employee("124", "Martin", "Chekov", 45000, "123");
        Employee manager2 = new Employee("125", "Bob", "Ronstad", 47000, "123");
        Employee emp1 = new Employee("300", "Alice", "Hasacat", 50000, "124");
        Employee emp2 = new Employee("305", "Brett", "Hardleaf", 34000, "300");

        ceo.addDirectReport(manager1);
        ceo.addDirectReport(manager2);
        manager1.setManager(ceo);
        manager2.setManager(ceo);

        manager1.addDirectReport(emp1);
        emp1.setManager(manager1);

        emp1.addDirectReport(emp2);
        emp2.setManager(emp1);

        List<Employee> employees = new ArrayList<>();
        employees.add(ceo);
        employees.add(manager1);
        employees.add(manager2);
        employees.add(emp1);
        employees.add(emp2);

        return employees;
    }

    private void buildChain(Employee... employees) {
        for (int i = 0; i < employees.length - 1; i++) {
            employees[i].addDirectReport(employees[i + 1]);
            employees[i + 1].setManager(employees[i]);
        }
    }
}