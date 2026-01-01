package com.example.sms.application.port.out;

import com.example.sms.domain.model.employee.Employee;

import java.util.List;
import java.util.Optional;

/**
 * 社員リポジトリ（Output Port）.
 */
public interface EmployeeRepository {

    void save(Employee employee);

    Optional<Employee> findByCode(String employeeCode);

    List<Employee> findAll();

    List<Employee> findByDepartmentCode(String departmentCode);

    void update(Employee employee);

    void deleteAll();
}
