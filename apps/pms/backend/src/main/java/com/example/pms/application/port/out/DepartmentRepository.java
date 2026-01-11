package com.example.pms.application.port.out;

import com.example.pms.domain.model.department.Department;

import java.util.List;
import java.util.Optional;

/**
 * 部門マスタリポジトリインターフェース.
 */
public interface DepartmentRepository {
    void save(Department department);
    Optional<Department> findByDepartmentCode(String departmentCode);
    List<Department> findAll();
    void update(Department department);
    void deleteAll();
}
