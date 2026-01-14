package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.MpsRepository;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import com.example.pms.infrastructure.out.persistence.mapper.MasterProductionScheduleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 基準生産計画リポジトリ実装
 */
@Repository
public class MpsRepositoryImpl implements MpsRepository {

    private final MasterProductionScheduleMapper mpsMapper;

    public MpsRepositoryImpl(MasterProductionScheduleMapper mpsMapper) {
        this.mpsMapper = mpsMapper;
    }

    @Override
    public void save(MasterProductionSchedule mps) {
        mpsMapper.insert(mps);
    }

    @Override
    public Optional<MasterProductionSchedule> findById(Integer id) {
        return Optional.ofNullable(mpsMapper.findById(id));
    }

    @Override
    public Optional<MasterProductionSchedule> findByMpsNumber(String mpsNumber) {
        return Optional.ofNullable(mpsMapper.findByMpsNumber(mpsNumber));
    }

    @Override
    public Optional<MasterProductionSchedule> findByMpsNumberWithOrders(String mpsNumber) {
        return Optional.ofNullable(mpsMapper.findByMpsNumberWithOrders(mpsNumber));
    }

    @Override
    public List<MasterProductionSchedule> findByStatus(PlanStatus status) {
        return mpsMapper.findByStatus(status);
    }

    @Override
    public List<MasterProductionSchedule> findAll() {
        return mpsMapper.findAll();
    }

    @Override
    public List<MasterProductionSchedule> findWithPagination(PlanStatus status, String keyword, int limit, int offset) {
        return mpsMapper.findWithPagination(status, keyword, limit, offset);
    }

    @Override
    public long count(PlanStatus status, String keyword) {
        return mpsMapper.count(status, keyword);
    }

    @Override
    public void update(MasterProductionSchedule mps) {
        mpsMapper.update(mps);
    }

    @Override
    public void updateStatus(Integer id, PlanStatus status) {
        mpsMapper.updateStatus(id, status);
    }

    @Override
    public void deleteAll() {
        mpsMapper.deleteAll();
    }
}
