package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.AllocationRepository;
import com.example.pms.domain.model.plan.Allocation;
import com.example.pms.infrastructure.out.persistence.mapper.AllocationMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 引当情報リポジトリ実装
 */
@Repository
public class AllocationRepositoryImpl implements AllocationRepository {

    private final AllocationMapper allocationMapper;

    public AllocationRepositoryImpl(AllocationMapper allocationMapper) {
        this.allocationMapper = allocationMapper;
    }

    @Override
    public void save(Allocation allocation) {
        allocationMapper.insert(allocation);
    }

    @Override
    public Optional<Allocation> findById(Integer id) {
        return Optional.ofNullable(allocationMapper.findById(id));
    }

    @Override
    public List<Allocation> findByRequirementId(Integer requirementId) {
        return allocationMapper.findByRequirementId(requirementId);
    }

    @Override
    public List<Allocation> findAll() {
        return allocationMapper.findAll();
    }

    @Override
    public void deleteAll() {
        allocationMapper.deleteAll();
    }
}
