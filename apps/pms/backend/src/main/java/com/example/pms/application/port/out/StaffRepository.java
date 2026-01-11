package com.example.pms.application.port.out;

import com.example.pms.domain.model.staff.Staff;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 担当者マスタリポジトリインターフェース.
 */
public interface StaffRepository {
    void save(Staff staff);
    Optional<Staff> findByStaffCode(String staffCode);
    Optional<Staff> findByStaffCodeAndDate(String staffCode, LocalDate baseDate);
    List<Staff> findByDepartmentCode(String departmentCode);
    List<Staff> findAll();
    void update(Staff staff);
    void deleteAll();
}
