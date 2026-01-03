package com.example.sms.application.service;

import com.example.sms.application.port.in.DepartmentUseCase;
import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.domain.exception.DepartmentNotFoundException;
import com.example.sms.domain.model.department.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 部門アプリケーションサービス.
 */
@Service
@Transactional
public class DepartmentService implements DepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentByCode(String departmentCode) {
        return departmentRepository.findByCode(departmentCode)
            .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentByCodeAndDate(String departmentCode, LocalDate baseDate) {
        return departmentRepository.findByCodeAndDate(departmentCode, baseDate)
            .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByHierarchyLevel(int level) {
        return departmentRepository.findByHierarchyLevel(level);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getChildDepartments(String parentPath) {
        return departmentRepository.findChildren(parentPath);
    }

    @Override
    public void createDepartment(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public void updateDepartment(Department department) {
        departmentRepository.update(department);
    }
}
