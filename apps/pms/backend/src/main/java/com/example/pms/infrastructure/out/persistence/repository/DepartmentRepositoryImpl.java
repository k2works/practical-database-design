package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.DepartmentRepository;
import com.example.pms.domain.model.department.Department;
import com.example.pms.infrastructure.out.persistence.mapper.DepartmentMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;

    public DepartmentRepositoryImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    public void save(Department department) {
        departmentMapper.insert(department);
    }

    @Override
    public Optional<Department> findByDepartmentCode(String departmentCode) {
        return departmentMapper.findByDepartmentCode(departmentCode);
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
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
