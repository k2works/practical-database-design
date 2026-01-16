package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.domain.model.department.Department;
import com.example.pms.domain.model.staff.Staff;
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

@DisplayName("担当者マスタリポジトリ")
class StaffRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        staffRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    private void setupDepartment() {
        departmentRepository.save(Department.builder()
                .departmentCode("D001")
                .departmentName("製造部")
                .validFrom(LocalDate.of(2024, 1, 1))
                .build());
    }

    private Staff createStaff(String code, String name, String deptCode) {
        return Staff.builder()
                .staffCode(code)
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .staffName(name)
                .departmentCode(deptCode)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("担当者を登録できる")
        void canRegisterStaff() {
            setupDepartment();
            Staff staff = createStaff("S001", "山田太郎", "D001");
            staffRepository.save(staff);

            Optional<Staff> found = staffRepository.findByStaffCode("S001");
            assertThat(found).isPresent();
            assertThat(found.get().getStaffName()).isEqualTo("山田太郎");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            setupDepartment();
            staffRepository.save(createStaff("S001", "山田太郎", "D001"));
            staffRepository.save(createStaff("S002", "鈴木花子", "D001"));
        }

        @Test
        @DisplayName("担当者コードで検索できる")
        void canFindByStaffCode() {
            Optional<Staff> found = staffRepository.findByStaffCode("S001");
            assertThat(found).isPresent();
            assertThat(found.get().getStaffName()).isEqualTo("山田太郎");
        }

        @Test
        @DisplayName("存在しない担当者コードで検索すると空を返す")
        void returnsEmptyForNonExistentStaffCode() {
            Optional<Staff> found = staffRepository.findByStaffCode("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("世代管理対応で日付検索できる")
        void canFindByStaffCodeAndDate() {
            staffRepository.deleteAll();
            staffRepository.save(Staff.builder()
                    .staffCode("S001")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(LocalDate.of(2024, 6, 30))
                    .staffName("山田太郎（旧）")
                    .departmentCode("D001")
                    .build());
            staffRepository.save(Staff.builder()
                    .staffCode("S001")
                    .effectiveFrom(LocalDate.of(2024, 7, 1))
                    .staffName("山田太郎（新）")
                    .departmentCode("D001")
                    .build());

            Optional<Staff> v1 = staffRepository.findByStaffCodeAndDate("S001", LocalDate.of(2024, 3, 1));
            Optional<Staff> v2 = staffRepository.findByStaffCodeAndDate("S001", LocalDate.of(2024, 8, 1));

            assertThat(v1).isPresent();
            assertThat(v1.get().getStaffName()).isEqualTo("山田太郎（旧）");
            assertThat(v2).isPresent();
            assertThat(v2.get().getStaffName()).isEqualTo("山田太郎（新）");
        }

        @Test
        @DisplayName("部門コードで検索できる")
        void canFindByDepartmentCode() {
            List<Staff> found = staffRepository.findByDepartmentCode("D001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<Staff> found = staffRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("担当者を更新できる")
        void canUpdateStaff() {
            setupDepartment();
            staffRepository.save(createStaff("S001", "山田太郎", "D001"));

            Optional<Staff> saved = staffRepository.findByStaffCode("S001");
            assertThat(saved).isPresent();
            Staff toUpdate = saved.get();
            toUpdate.setEmail("yamada@example.com");
            staffRepository.update(toUpdate);

            Optional<Staff> updated = staffRepository.findByStaffCode("S001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getEmail()).isEqualTo("yamada@example.com");
        }
    }
}
