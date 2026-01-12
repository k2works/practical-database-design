package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkOrderDetailRepository;
import com.example.pms.domain.model.process.WorkOrderDetail;
import com.example.pms.infrastructure.out.persistence.mapper.WorkOrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作業指示明細リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class WorkOrderDetailRepositoryImpl implements WorkOrderDetailRepository {

    private final WorkOrderDetailMapper workOrderDetailMapper;

    @Override
    public void save(WorkOrderDetail workOrderDetail) {
        workOrderDetailMapper.insert(workOrderDetail);
    }

    @Override
    public Optional<WorkOrderDetail> findById(Integer id) {
        return Optional.ofNullable(workOrderDetailMapper.findById(id));
    }

    @Override
    public Optional<WorkOrderDetail> findByWorkOrderNumberAndSequence(
            String workOrderNumber, Integer sequence) {
        return Optional.ofNullable(
                workOrderDetailMapper.findByWorkOrderNumberAndSequence(workOrderNumber, sequence));
    }

    @Override
    public List<WorkOrderDetail> findByWorkOrderNumber(String workOrderNumber) {
        return workOrderDetailMapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<WorkOrderDetail> findAll() {
        return workOrderDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        workOrderDetailMapper.deleteAll();
    }
}
