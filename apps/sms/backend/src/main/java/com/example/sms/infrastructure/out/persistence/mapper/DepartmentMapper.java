package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.department.Department;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 部門マッパー.
 */
@Mapper
public interface DepartmentMapper {

    void insert(Department department);

    Optional<Department> findByCode(String departmentCode);

    Optional<Department> findByCodeAndDate(String departmentCode, LocalDate baseDate);

    List<Department> findAll();

    List<Department> findByHierarchyLevel(int level);

    List<Department> findByPathPrefix(String pathPrefix);

    void update(Department department);

    void deleteAll();
}
