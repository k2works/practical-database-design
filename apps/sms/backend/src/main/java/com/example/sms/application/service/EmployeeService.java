package com.example.sms.application.service;

import com.example.sms.application.port.in.EmployeeUseCase;
import com.example.sms.application.port.out.EmployeeRepository;
import com.example.sms.domain.exception.EmployeeNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.employee.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 社員アプリケーションサービス.
 */
@Service
@Transactional
public class EmployeeService implements EmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Employee> getEmployees(int page, int size, String departmentCode, String keyword) {
        return employeeRepository.findWithPagination(page, size, departmentCode, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeByCode(String employeeCode) {
        return employeeRepository.findByCode(employeeCode)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(String departmentCode) {
        return employeeRepository.findByDepartmentCode(departmentCode);
    }

    @Override
    public void createEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public void updateEmployee(Employee employee) {
        employeeRepository.update(employee);
    }
}
