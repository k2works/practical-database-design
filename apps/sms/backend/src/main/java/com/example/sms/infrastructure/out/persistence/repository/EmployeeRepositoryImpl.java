package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.EmployeeRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.employee.Employee;
import com.example.sms.infrastructure.out.persistence.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 社員リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeMapper employeeMapper;

    @Override
    public void save(Employee employee) {
        employeeMapper.insert(employee);
    }

    @Override
    public Optional<Employee> findByCode(String employeeCode) {
        return employeeMapper.findByCode(employeeCode);
    }

    @Override
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    @Override
    public List<Employee> findByDepartmentCode(String departmentCode) {
        return employeeMapper.findByDepartmentCode(departmentCode);
    }

    @Override
    public PageResult<Employee> findWithPagination(int page, int size, String departmentCode, String keyword) {
        int offset = page * size;
        List<Employee> employees = employeeMapper.findWithPagination(offset, size, departmentCode, keyword);
        long totalElements = employeeMapper.count(departmentCode, keyword);
        return new PageResult<>(employees, page, size, totalElements);
    }

    @Override
    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    @Override
    public void deleteAll() {
        employeeMapper.deleteAll();
    }
}
