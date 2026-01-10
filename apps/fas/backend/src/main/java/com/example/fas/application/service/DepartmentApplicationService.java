package com.example.fas.application.service;

import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.command.CreateDepartmentCommand;
import com.example.fas.application.port.in.command.UpdateDepartmentCommand;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import com.example.fas.application.port.out.DepartmentRepository;
import com.example.fas.domain.exception.DepartmentAlreadyExistsException;
import com.example.fas.domain.exception.DepartmentNotFoundException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.department.Department;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 部門アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentApplicationService implements DepartmentUseCase {

    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponse getDepartment(String departmentCode) {
        Department department = departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));
        return DepartmentResponse.from(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Override
    public PageResult<DepartmentResponse> getDepartments(int page, int size, String keyword, Integer level) {
        PageResult<Department> pageResult = departmentRepository.findWithPagination(page, size, keyword, level);
        List<DepartmentResponse> content = pageResult.getContent().stream()
                .map(DepartmentResponse::from)
                .toList();
        return new PageResult<>(content, pageResult.getPage(), pageResult.getSize(), pageResult.getTotalElements());
    }

    @Override
    public List<DepartmentResponse> getLowestLevelDepartments() {
        return departmentRepository.findLowestLevel().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentCommand command) {
        if (departmentRepository.findByCode(command.departmentCode()).isPresent()) {
            throw new DepartmentAlreadyExistsException(command.departmentCode());
        }

        Department department = Department.builder()
                .departmentCode(command.departmentCode())
                .departmentName(command.departmentName())
                .departmentShortName(command.departmentShortName())
                .organizationLevel(command.organizationLevel())
                .departmentPath(command.departmentPath())
                .lowestLevelFlag(command.lowestLevelFlag())
                .build();

        departmentRepository.save(department);

        return DepartmentResponse.from(department);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String departmentCode, UpdateDepartmentCommand command) {
        Department department = departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));

        if (command.departmentName() != null) {
            department.setDepartmentName(command.departmentName());
        }
        if (command.departmentShortName() != null) {
            department.setDepartmentShortName(command.departmentShortName());
        }
        if (command.organizationLevel() != null) {
            department.setOrganizationLevel(command.organizationLevel());
        }
        if (command.departmentPath() != null) {
            department.setDepartmentPath(command.departmentPath());
        }
        if (command.lowestLevelFlag() != null) {
            department.setLowestLevelFlag(command.lowestLevelFlag());
        }

        departmentRepository.update(department);

        return DepartmentResponse.from(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(String departmentCode) {
        departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));
        departmentRepository.deleteByCode(departmentCode);
    }
}
