package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.employee.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 社員マッパー.
 */
@Mapper
public interface EmployeeMapper {

    void insert(Employee employee);

    Optional<Employee> findByCode(String employeeCode);

    List<Employee> findAll();

    List<Employee> findByDepartmentCode(String departmentCode);

    void update(Employee employee);

    void deleteAll();
}
