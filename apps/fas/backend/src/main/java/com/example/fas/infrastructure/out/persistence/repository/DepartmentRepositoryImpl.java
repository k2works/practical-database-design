package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.DepartmentRepository;
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
    public void deleteByCode(String departmentCode) {
        departmentMapper.deleteByCode(departmentCode);
    }

    @Override
    public void deleteAll() {
        departmentMapper.deleteAll();
    }
}
