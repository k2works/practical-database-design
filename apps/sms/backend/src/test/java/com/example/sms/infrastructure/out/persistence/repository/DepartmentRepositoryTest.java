package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.domain.model.department.Department;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 部門リポジトリテスト.
 */
@DisplayName("部門リポジトリ")
class DepartmentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("部門を登録できる")
        void canRegisterDepartment() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("10000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("本社")
                    .hierarchyLevel(0)
                    .departmentPath("10000")
                    .isLeaf(false)
                    .build();

            // Act
            departmentRepository.save(department);

            // Assert
            var result = departmentRepository.findByCode("10000");
            assertThat(result).isPresent();
            assertThat(result.get().getDepartmentName()).isEqualTo("本社");
            assertThat(result.get().getHierarchyLevel()).isEqualTo(0);
        }

        @Test
        @DisplayName("階層構造を持つ部門を登録できる")
        void canRegisterHierarchicalDepartments() {
            // Arrange: 親部門
            var parent = Department.builder()
                    .departmentCode("10000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("本社")
                    .hierarchyLevel(0)
                    .departmentPath("10000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(parent);

            // Arrange: 子部門
            var child = Department.builder()
                    .departmentCode("11000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("営業本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~11000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(child);

            // Arrange: 孫部門（最下層）
            var grandChild = Department.builder()
                    .departmentCode("11101")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("営業1課")
                    .hierarchyLevel(3)
                    .departmentPath("10000~11000~11100~11101")
                    .isLeaf(true)
                    .build();
            departmentRepository.save(grandChild);

            // Act
            var result = departmentRepository.findByCode("11101");

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getDepartmentPath()).isEqualTo("10000~11000~11100~11101");
            assertThat(result.get().isLeaf()).isTrue();
        }
    }

    @Nested
    @DisplayName("履歴管理")
    class HistoryManagement {

        @Test
        @DisplayName("同じ部門コードでも開始日が異なれば登録できる（組織改正対応）")
        void canRegisterSameCodeWithDifferentStartDate() {
            // Arrange: 旧組織
            var oldDept = Department.builder()
                    .departmentCode("11000")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 12, 31))
                    .departmentName("営業部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~11000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(oldDept);

            // Arrange: 新組織
            var newDept = Department.builder()
                    .departmentCode("11000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("営業本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~11000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(newDept);

            // Act: 現在有効な部門を取得
            var result = departmentRepository.findByCodeAndDate("11000", LocalDate.of(2025, 4, 1));

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getDepartmentName()).isEqualTo("営業本部");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("階層レベルで部門を検索できる")
        void canFindByHierarchyLevel() {
            // Arrange
            var root = Department.builder()
                    .departmentCode("10000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("本社")
                    .hierarchyLevel(0)
                    .departmentPath("10000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(root);

            var level1Dept1 = Department.builder()
                    .departmentCode("11000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("営業本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~11000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(level1Dept1);

            var level1Dept2 = Department.builder()
                    .departmentCode("12000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("管理本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~12000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(level1Dept2);

            // Act
            var level1Departments = departmentRepository.findByHierarchyLevel(1);

            // Assert
            assertThat(level1Departments).hasSize(2);
            assertThat(level1Departments)
                    .extracting(Department::getDepartmentName)
                    .containsExactlyInAnyOrder("営業本部", "管理本部");
        }

        @Test
        @DisplayName("親パスから子部門を検索できる")
        void canFindChildren() {
            // Arrange
            var parent = Department.builder()
                    .departmentCode("10000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("本社")
                    .hierarchyLevel(0)
                    .departmentPath("10000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(parent);

            var child1 = Department.builder()
                    .departmentCode("11000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("営業本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~11000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(child1);

            var child2 = Department.builder()
                    .departmentCode("12000")
                    .startDate(LocalDate.of(2025, 1, 1))
                    .departmentName("管理本部")
                    .hierarchyLevel(1)
                    .departmentPath("10000~12000")
                    .isLeaf(false)
                    .build();
            departmentRepository.save(child2);

            // Act
            var children = departmentRepository.findChildren("10000");

            // Assert
            assertThat(children).hasSize(2);
        }
    }
}
