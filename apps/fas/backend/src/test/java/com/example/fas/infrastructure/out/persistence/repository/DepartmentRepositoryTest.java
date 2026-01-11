package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fas.application.port.out.DepartmentRepository;
import com.example.fas.domain.model.department.Department;
import com.example.fas.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    @DisplayName("登録と検索")
    class InsertAndFindTests {

        @Test
        @DisplayName("部門を登録して検索できる")
        void canInsertAndFindDepartment() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("10000")
                    .departmentName("全社")
                    .departmentShortName("全社")
                    .organizationLevel(0)
                    .departmentPath("10000")
                    .lowestLevelFlag(0)
                    .build();

            // Act
            departmentRepository.save(department);

            // Assert
            var result = departmentRepository.findByCode("10000");
            assertThat(result).isPresent();
            assertThat(result.get().getDepartmentName()).isEqualTo("全社");
            assertThat(result.get().getOrganizationLevel()).isZero();
        }

        @Test
        @DisplayName("存在しない部門コードを検索するとemptyが返る")
        void returnsEmptyWhenNotFound() {
            // Act
            var result = departmentRepository.findByCode("99999");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("階層検索")
    class HierarchySearchTests {

        @BeforeEach
        void setUpTestData() {
            // 全社（レベル0）
            departmentRepository.save(Department.builder()
                    .departmentCode("10000")
                    .departmentName("全社")
                    .organizationLevel(0)
                    .departmentPath("10000")
                    .lowestLevelFlag(0)
                    .build());

            // 営業本部（レベル1）
            departmentRepository.save(Department.builder()
                    .departmentCode("11000")
                    .departmentName("営業本部")
                    .organizationLevel(1)
                    .departmentPath("10000~11000")
                    .lowestLevelFlag(0)
                    .build());

            // 東日本営業部（レベル2）
            departmentRepository.save(Department.builder()
                    .departmentCode("11100")
                    .departmentName("東日本営業部")
                    .organizationLevel(2)
                    .departmentPath("10000~11000~11100")
                    .lowestLevelFlag(0)
                    .build());

            // 東日本営業課（レベル3、最下層）
            departmentRepository.save(Department.builder()
                    .departmentCode("11110")
                    .departmentName("東日本営業課")
                    .organizationLevel(3)
                    .departmentPath("10000~11000~11100~11110")
                    .lowestLevelFlag(1)
                    .build());

            // 製造本部（レベル1）
            departmentRepository.save(Department.builder()
                    .departmentCode("12000")
                    .departmentName("製造本部")
                    .organizationLevel(1)
                    .departmentPath("10000~12000")
                    .lowestLevelFlag(0)
                    .build());
        }

        @Test
        @DisplayName("全部門を取得できる")
        void canFindAll() {
            // Act
            var result = departmentRepository.findAll();

            // Assert
            assertThat(result).hasSize(5);
        }

        @Test
        @DisplayName("組織階層で検索できる")
        void canFindByOrganizationLevel() {
            // Act
            var result = departmentRepository.findByOrganizationLevel(1);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Department::getDepartmentName)
                    .containsExactlyInAnyOrder("営業本部", "製造本部");
        }

        @Test
        @DisplayName("最下層部門のみ取得できる")
        void canFindLowestLevel() {
            // Act
            var result = departmentRepository.findLowestLevel();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDepartmentName()).isEqualTo("東日本営業課");
        }

        @Test
        @DisplayName("パス接頭辞で配下部門を検索できる")
        void canFindByPathPrefix() {
            // Act
            var result = departmentRepository.findByPathPrefix("10000~11000");

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Department::getDepartmentName)
                    .containsExactlyInAnyOrder("営業本部", "東日本営業部", "東日本営業課");
        }
    }

    @Nested
    @DisplayName("削除")
    class DeleteTests {

        @Test
        @DisplayName("部門を削除できる")
        void canDeleteDepartment() {
            // Arrange
            departmentRepository.save(Department.builder()
                    .departmentCode("99999")
                    .departmentName("テスト部門")
                    .organizationLevel(1)
                    .departmentPath("10000~99999")
                    .lowestLevelFlag(0)
                    .build());

            // Act
            departmentRepository.deleteByCode("99999");

            // Assert
            var result = departmentRepository.findByCode("99999");
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("エンティティのメソッド")
    class EntityMethodTests {

        @Test
        @DisplayName("上位部門コードのリストを取得できる")
        void canGetAncestorCodes() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("11110")
                    .departmentPath("10000~11000~11100~11110")
                    .build();

            // Act
            var result = department.getAncestorCodes();

            // Assert
            assertThat(result).containsExactly("10000", "11000", "11100", "11110");
        }

        @Test
        @DisplayName("親部門コードを取得できる")
        void canGetParentCode() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("11110")
                    .departmentPath("10000~11000~11100~11110")
                    .build();

            // Act
            var result = department.getParentCode();

            // Assert
            assertThat(result).isEqualTo("11100");
        }

        @Test
        @DisplayName("ルート部門の親コードはnull")
        void rootDepartmentHasNoParent() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("10000")
                    .departmentPath("10000")
                    .build();

            // Act
            var result = department.getParentCode();

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("指定部門の下位かどうか判定できる")
        void canCheckIfDescendantOf() {
            // Arrange
            var department = Department.builder()
                    .departmentCode("11110")
                    .departmentPath("10000~11000~11100~11110")
                    .build();

            // Assert
            assertThat(department.isDescendantOf("11000")).isTrue();
            assertThat(department.isDescendantOf("12000")).isFalse();
        }
    }
}
