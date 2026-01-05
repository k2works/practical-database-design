package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.application.port.out.EmployeeRepository;
import com.example.sms.domain.model.department.Department;
import com.example.sms.domain.model.employee.Employee;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 社員リポジトリテスト.
 */
@DisplayName("社員リポジトリ")
class EmployeeRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private static final LocalDate DEPT_START_DATE = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        // 部門マスタを登録
        var department = Department.builder()
                .departmentCode("10000")
                .startDate(DEPT_START_DATE)
                .departmentName("本社")
                .hierarchyLevel(0)
                .departmentPath("10000")
                .isLeaf(false)
                .build();
        departmentRepository.save(department);
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("社員を登録できる")
        void canRegisterEmployee() {
            // Arrange
            var employee = Employee.builder()
                    .employeeCode("EMP001")
                    .employeeName("山田太郎")
                    .employeeNameKana("ヤマダタロウ")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();

            // Act
            employeeRepository.save(employee);

            // Assert
            var result = employeeRepository.findByCode("EMP001");
            assertThat(result).isPresent();
            assertThat(result.get().getEmployeeName()).isEqualTo("山田太郎");
            assertThat(result.get().getEmployeeNameKana()).isEqualTo("ヤマダタロウ");
            assertThat(result.get().getDepartmentCode()).isEqualTo("10000");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("部門コードで社員を検索できる")
        void canFindByDepartmentCode() {
            // Arrange
            var employee1 = Employee.builder()
                    .employeeCode("EMP001")
                    .employeeName("山田太郎")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();
            employeeRepository.save(employee1);

            var employee2 = Employee.builder()
                    .employeeCode("EMP002")
                    .employeeName("佐藤花子")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();
            employeeRepository.save(employee2);

            // Act
            var employees = employeeRepository.findByDepartmentCode("10000");

            // Assert
            assertThat(employees).hasSize(2);
            assertThat(employees)
                    .extracting(Employee::getEmployeeName)
                    .containsExactlyInAnyOrder("山田太郎", "佐藤花子");
        }

        @Test
        @DisplayName("全社員を取得できる")
        void canFindAll() {
            // Arrange
            var employee1 = Employee.builder()
                    .employeeCode("EMP001")
                    .employeeName("山田太郎")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();
            employeeRepository.save(employee1);

            var employee2 = Employee.builder()
                    .employeeCode("EMP002")
                    .employeeName("佐藤花子")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();
            employeeRepository.save(employee2);

            // Act
            var employees = employeeRepository.findAll();

            // Assert
            assertThat(employees).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("社員情報を更新できる")
        void canUpdateEmployee() {
            // Arrange
            var employee = Employee.builder()
                    .employeeCode("EMP001")
                    .employeeName("山田太郎")
                    .departmentCode("10000")
                    .departmentStartDate(DEPT_START_DATE)
                    .build();
            employeeRepository.save(employee);

            // Act
            employee.setEmployeeName("山田次郎");
            employee.setEmployeeNameKana("ヤマダジロウ");
            employeeRepository.update(employee);

            // Assert
            var result = employeeRepository.findByCode("EMP001");
            assertThat(result).isPresent();
            assertThat(result.get().getEmployeeName()).isEqualTo("山田次郎");
            assertThat(result.get().getEmployeeNameKana()).isEqualTo("ヤマダジロウ");
        }
    }
}
