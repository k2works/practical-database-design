package com.example.pms.application.service;

import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.application.port.in.command.CreateDepartmentCommand;
import com.example.pms.application.port.in.command.UpdateDepartmentCommand;
import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.department.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 部門サービス（Application Service）.
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
    public PageResult<Department> getDepartments(int page, int size, String keyword) {
        int offset = page * size;
        List<Department> departments = departmentRepository.findWithPagination(keyword, size, offset);
        long totalElements = departmentRepository.count(keyword);
        return new PageResult<>(departments, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department createDepartment(CreateDepartmentCommand command) {
        Department department = Department.builder()
            .departmentCode(command.getDepartmentCode())
            .departmentName(command.getDepartmentName())
            .departmentPath(command.getDepartmentPath())
            .lowestLevel(command.getLowestLevel())
            .validFrom(command.getValidFrom())
            .validTo(command.getValidTo())
            .build();
        departmentRepository.save(department);
        return department;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Department> getDepartment(String departmentCode) {
        return departmentRepository.findByDepartmentCode(departmentCode);
    }

    @Override
    public Department updateDepartment(String departmentCode, UpdateDepartmentCommand command) {
        Department department = Department.builder()
            .departmentCode(departmentCode)
            .departmentName(command.getDepartmentName())
            .departmentPath(command.getDepartmentPath())
            .lowestLevel(command.getLowestLevel())
            .validFrom(command.getValidFrom())
            .validTo(command.getValidTo())
            .build();
        departmentRepository.update(department);
        return department;
    }

    @Override
    public void deleteDepartment(String departmentCode) {
        departmentRepository.deleteByDepartmentCode(departmentCode);
    }
}
