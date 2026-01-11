package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.plan.Allocation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AllocationMapper {
    void insert(Allocation allocation);
    Allocation findById(Integer id);
    List<Allocation> findByRequirementId(Integer requirementId);
    List<Allocation> findAll();
    void deleteAll();
}
