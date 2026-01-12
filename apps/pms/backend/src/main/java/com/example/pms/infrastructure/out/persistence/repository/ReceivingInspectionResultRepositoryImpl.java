package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ReceivingInspectionResultRepository;
import com.example.pms.domain.model.quality.ReceivingInspectionResult;
import com.example.pms.infrastructure.out.persistence.mapper.ReceivingInspectionResultMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査結果リポジトリ実装.
 */
@Repository
public class ReceivingInspectionResultRepositoryImpl implements ReceivingInspectionResultRepository {

    private final ReceivingInspectionResultMapper receivingInspectionResultMapper;

    public ReceivingInspectionResultRepositoryImpl(ReceivingInspectionResultMapper receivingInspectionResultMapper) {
        this.receivingInspectionResultMapper = receivingInspectionResultMapper;
    }

    @Override
    public void save(ReceivingInspectionResult result) {
        receivingInspectionResultMapper.insert(result);
    }

    @Override
    public Optional<ReceivingInspectionResult> findById(Integer id) {
        return Optional.ofNullable(receivingInspectionResultMapper.findById(id));
    }

    @Override
    public List<ReceivingInspectionResult> findByInspectionNumber(String inspectionNumber) {
        return receivingInspectionResultMapper.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public void update(ReceivingInspectionResult result) {
        receivingInspectionResultMapper.update(result);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        receivingInspectionResultMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteById(Integer id) {
        receivingInspectionResultMapper.deleteById(id);
    }
}
