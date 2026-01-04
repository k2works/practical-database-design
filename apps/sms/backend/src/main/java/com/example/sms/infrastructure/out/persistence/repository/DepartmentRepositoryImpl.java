package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.DepartmentRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.department.Department;
import com.example.sms.infrastructure.out.persistence.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 部門リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;

    @Override
    public void save(Department department) {
        departmentMapper.insert(department);
    }

    @Override
    public Optional<Department> findByCode(String departmentCode) {
        return departmentMapper.findByCode(departmentCode);
    }

    @Override
    public Optional<Department> findByCodeAndDate(String departmentCode, LocalDate baseDate) {
        return departmentMapper.findByCodeAndDate(departmentCode, baseDate);
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Override
    public List<Department> findByHierarchyLevel(int level) {
        return departmentMapper.findByHierarchyLevel(level);
    }

    @Override
    public List<Department> findChildren(String parentPath) {
        return departmentMapper.findByPathPrefix(parentPath);
    }

    @Override
    public PageResult<Department> findWithPagination(int page, int size, Integer level, String keyword) {
        int offset = page * size;
        List<Department> departments = departmentMapper.findWithPagination(offset, size, level, keyword);
        long totalElements = departmentMapper.count(level, keyword);
        return new PageResult<>(departments, page, size, totalElements);
    }

    @Override
    public void update(Department department) {
        departmentMapper.update(department);
    }

    @Override
    public void deleteAll() {
        departmentMapper.deleteAll();
    }
}
