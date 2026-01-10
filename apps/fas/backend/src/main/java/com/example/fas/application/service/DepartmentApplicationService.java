package com.example.fas.application.service;

import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.dto.CreateDepartmentCommand;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import com.example.fas.application.port.in.dto.UpdateDepartmentCommand;
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
        if (departmentRepository.findByCode(command.getDepartmentCode()).isPresent()) {
            throw new DepartmentAlreadyExistsException(command.getDepartmentCode());
        }

        Department department = Department.builder()
                .departmentCode(command.getDepartmentCode())
                .departmentName(command.getDepartmentName())
                .departmentShortName(command.getDepartmentShortName())
                .organizationLevel(command.getOrganizationLevel())
                .departmentPath(command.getDepartmentPath())
                .lowestLevelFlag(command.getLowestLevelFlag())
                .build();

        departmentRepository.save(department);

        return DepartmentResponse.from(department);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String departmentCode, UpdateDepartmentCommand command) {
        Department department = departmentRepository.findByCode(departmentCode)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentCode));

        if (command.getDepartmentName() != null) {
            department.setDepartmentName(command.getDepartmentName());
        }
        if (command.getDepartmentShortName() != null) {
            department.setDepartmentShortName(command.getDepartmentShortName());
        }
        if (command.getOrganizationLevel() != null) {
            department.setOrganizationLevel(command.getOrganizationLevel());
        }
        if (command.getDepartmentPath() != null) {
            department.setDepartmentPath(command.getDepartmentPath());
        }
        if (command.getLowestLevelFlag() != null) {
            department.setLowestLevelFlag(command.getLowestLevelFlag());
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
