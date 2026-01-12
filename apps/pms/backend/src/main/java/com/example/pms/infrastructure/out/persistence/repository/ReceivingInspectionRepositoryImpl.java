package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ReceivingInspectionRepository;
import com.example.pms.domain.model.quality.ReceivingInspection;
import com.example.pms.infrastructure.out.persistence.mapper.ReceivingInspectionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査リポジトリ実装.
 */
@Repository
public class ReceivingInspectionRepositoryImpl implements ReceivingInspectionRepository {

    private final ReceivingInspectionMapper receivingInspectionMapper;

    public ReceivingInspectionRepositoryImpl(ReceivingInspectionMapper receivingInspectionMapper) {
        this.receivingInspectionMapper = receivingInspectionMapper;
    }

    @Override
    public void save(ReceivingInspection inspection) {
        receivingInspectionMapper.insert(inspection);
    }

    @Override
    public Optional<ReceivingInspection> findById(Integer id) {
        return Optional.ofNullable(receivingInspectionMapper.findById(id));
    }

    @Override
    public Optional<ReceivingInspection> findByInspectionNumber(String inspectionNumber) {
        return Optional.ofNullable(receivingInspectionMapper.findByInspectionNumber(inspectionNumber));
    }

    @Override
    public Optional<ReceivingInspection> findByInspectionNumberWithResults(String inspectionNumber) {
        return Optional.ofNullable(receivingInspectionMapper.findByInspectionNumberWithResults(inspectionNumber));
    }

    @Override
    public List<ReceivingInspection> findByReceivingNumber(String receivingNumber) {
        return receivingInspectionMapper.findByReceivingNumber(receivingNumber);
    }

    @Override
    public List<ReceivingInspection> findBySupplierCode(String supplierCode) {
        return receivingInspectionMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<ReceivingInspection> findAll() {
        return receivingInspectionMapper.findAll();
    }

    @Override
    public int update(ReceivingInspection inspection) {
        return receivingInspectionMapper.update(inspection);
    }

    @Override
    public int updateJudgment(ReceivingInspection inspection) {
        return receivingInspectionMapper.updateJudgment(inspection);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        receivingInspectionMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteAll() {
        receivingInspectionMapper.deleteAll();
    }
}
