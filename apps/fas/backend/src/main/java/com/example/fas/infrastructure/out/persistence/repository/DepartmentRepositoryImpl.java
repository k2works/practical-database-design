package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.DepartmentRepository;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.department.Department;
import com.example.fas.infrastructure.out.persistence.mapper.DepartmentMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 部門リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;

    @Override
    public Optional<Department> findByCode(String departmentCode) {
        return departmentMapper.findByCode(departmentCode);
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Override
    public PageResult<Department> findWithPagination(int page, int size, String keyword, Integer level) {
        int offset = page * size;
        List<Department> content = departmentMapper.findWithPagination(offset, size, keyword, level);
        long totalElements = departmentMapper.count(keyword, level);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    public List<Department> findByOrganizationLevel(int level) {
        return departmentMapper.findByOrganizationLevel(level);
    }

    @Override
    public List<Department> findLowestLevel() {
        return departmentMapper.findLowestLevel();
    }

    @Override
    public List<Department> findByPathPrefix(String pathPrefix) {
        return departmentMapper.findByPathPrefix(pathPrefix);
    }

    @Override
    public void save(Department department) {
        departmentMapper.insert(department);
    }

    @Override
    public void update(Department department) {
        departmentMapper.update(department);
    }

    @Override
    public void deleteByCode(String departmentCode) {
        departmentMapper.deleteByCode(departmentCode);
    }

    @Override
    public void deleteAll() {
        departmentMapper.deleteAll();
    }
}
