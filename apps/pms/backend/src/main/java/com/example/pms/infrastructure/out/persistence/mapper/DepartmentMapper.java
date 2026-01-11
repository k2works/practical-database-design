package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.department.Department;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DepartmentMapper {
    void insert(Department department);
    Optional<Department> findByDepartmentCode(String departmentCode);
    List<Department> findAll();
    void update(Department department);
    void deleteAll();
}
