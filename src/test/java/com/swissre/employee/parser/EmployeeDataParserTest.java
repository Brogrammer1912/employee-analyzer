package com.swissre.employee.parser;

import com.swissre.employee.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDataParserTest {

    private EmployeeDataParser parser;

    @BeforeEach
    void setUp() {
        parser = new EmployeeDataParser();
    }

    @Test
    void testParseValidEmployeeData(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,60000,\n" +
                        "124,Martin,Chekov,45000,123\n" +
                        "125,Bob,Ronstad,47000,123"
        );

        List<Employee> employees = parser.parseEmployeeData(csvFile.toString());

        assertEquals(3, employees.size());

        Employee ceo = findEmployeeById(employees, "123");
        assertNotNull(ceo);
        assertEquals("Joe", ceo.getFirstName());
        assertEquals("Doe", ceo.getLastName());
        assertEquals(60000, ceo.getSalary());
        assertTrue(ceo.isCEO());
        assertEquals(2, ceo.getDirectReports().size());
    }

    @Test
    void testBuildHierarchy(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "1,CEO,Person,100000,\n" +
                        "2,Manager,One,80000,1\n" +
                        "3,Employee,One,50000,2"
        );

        List<Employee> employees = parser.parseEmployeeData(csvFile.toString());

        Employee ceo = findEmployeeById(employees, "1");
        Employee manager = findEmployeeById(employees, "2");
        Employee employee = findEmployeeById(employees, "3");

        assertNotNull(ceo.getDirectReports());
        assertEquals(1, ceo.getDirectReports().size());
        assertTrue(ceo.getDirectReports().contains(manager));

        assertEquals(ceo, manager.getManager());
        assertEquals(manager, employee.getManager());
    }

    @Test
    void testInvalidSalaryFormat(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,invalid,\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseEmployeeData(csvFile.toString())
        );
    }

    @Test
    void testNegativeSalary(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,-1000,\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseEmployeeData(csvFile.toString())
        );
    }

    @Test
    void testMissingManagerId(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,60000,999\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseEmployeeData(csvFile.toString())
        );
    }

    @Test
    void testEmptyEmployeeId(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        ",Joe,Doe,60000,\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseEmployeeData(csvFile.toString())
        );
    }

    @Test
    void testInsufficientColumns(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,60000\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                parser.parseEmployeeData(csvFile.toString())
        );
    }

    @Test
    void testSkipEmptyLines(@TempDir Path tempDir) throws IOException {
        Path csvFile = createTestCsvFile(tempDir,
                "Id,firstName,lastName,salary,managerId\n" +
                        "123,Joe,Doe,60000,\n" +
                        "\n" +
                        "124,Martin,Chekov,45000,123\n"
        );

        List<Employee> employees = parser.parseEmployeeData(csvFile.toString());
        assertEquals(2, employees.size());
    }

    private Path createTestCsvFile(Path tempDir, String content) throws IOException {
        Path csvFile = tempDir.resolve("test_employees.csv");
        Files.writeString(csvFile, content);
        return csvFile;
    }

    private Employee findEmployeeById(List<Employee> employees, String id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}