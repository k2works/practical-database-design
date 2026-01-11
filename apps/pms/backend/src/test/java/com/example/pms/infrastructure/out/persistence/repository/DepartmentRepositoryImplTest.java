package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.domain.model.department.Department;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("部門マスタリポジトリ")
class DepartmentRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
    }

    private Department createDepartment(String code, String name, String path) {
        return Department.builder()
                .departmentCode(code)
                .departmentName(name)
                .departmentPath(path)
                .lowestLevel(true)
                .validFrom(LocalDate.of(2024, 1, 1))
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("部門を登録できる")
        void canRegisterDepartment() {
            Department dept = createDepartment("D001", "製造部", "本社/製造部");
            departmentRepository.save(dept);

            Optional<Department> found = departmentRepository.findByDepartmentCode("D001");
            assertThat(found).isPresent();
            assertThat(found.get().getDepartmentName()).isEqualTo("製造部");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            departmentRepository.save(createDepartment("D001", "製造部", "本社/製造部"));
            departmentRepository.save(createDepartment("D002", "営業部", "本社/営業部"));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<Department> found = departmentRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("部門を更新できる")
        void canUpdateDepartment() {
            departmentRepository.save(createDepartment("D001", "製造部", "本社/製造部"));

            Optional<Department> saved = departmentRepository.findByDepartmentCode("D001");
            assertThat(saved).isPresent();
            Department toUpdate = saved.get();
            toUpdate.setDepartmentName("製造部（更新）");
            departmentRepository.update(toUpdate);

            Optional<Department> updated = departmentRepository.findByDepartmentCode("D001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getDepartmentName()).isEqualTo("製造部（更新）");
        }
    }
}
