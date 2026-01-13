package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.department.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DepartmentMapper {
    void insert(Department department);
    Optional<Department> findByDepartmentCode(String departmentCode);
    List<Department> findAll();
    void update(Department department);
    void deleteByDepartmentCode(String departmentCode);
    void deleteAll();
    List<Department> findWithPagination(@Param("keyword") String keyword,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);
    long count(@Param("keyword") String keyword);
}
