package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MasterProductionScheduleMapper {
    void insert(MasterProductionSchedule mps);
    MasterProductionSchedule findById(Integer id);
    MasterProductionSchedule findByMpsNumber(String mpsNumber);
    List<MasterProductionSchedule> findByStatus(PlanStatus status);
    List<MasterProductionSchedule> findAll();
    void updateStatus(@Param("id") Integer id, @Param("status") PlanStatus status);
    void deleteAll();
}
