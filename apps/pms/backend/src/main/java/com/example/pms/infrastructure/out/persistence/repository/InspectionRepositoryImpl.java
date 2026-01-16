package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.InspectionRepository;
import com.example.pms.domain.model.purchase.Inspection;
import com.example.pms.infrastructure.out.persistence.mapper.InspectionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査データリポジトリ実装
 */
@Repository
public class InspectionRepositoryImpl implements InspectionRepository {

    private final InspectionMapper inspectionMapper;

    public InspectionRepositoryImpl(InspectionMapper inspectionMapper) {
        this.inspectionMapper = inspectionMapper;
    }

    @Override
    public void save(Inspection inspection) {
        inspectionMapper.insert(inspection);
    }

    @Override
    public Optional<Inspection> findById(Integer id) {
        return Optional.ofNullable(inspectionMapper.findById(id));
    }

    @Override
    public Optional<Inspection> findByInspectionNumber(String inspectionNumber) {
        return Optional.ofNullable(inspectionMapper.findByInspectionNumber(inspectionNumber));
    }

    @Override
    public Optional<Inspection> findByInspectionNumberWithAcceptances(String inspectionNumber) {
        return Optional.ofNullable(inspectionMapper.findByInspectionNumberWithAcceptances(inspectionNumber));
    }

    @Override
    public List<Inspection> findByReceivingNumber(String receivingNumber) {
        return inspectionMapper.findByReceivingNumber(receivingNumber);
    }

    @Override
    public List<Inspection> findAll() {
        return inspectionMapper.findAll();
    }

    @Override
    public void deleteAll() {
        inspectionMapper.deleteAll();
    }
}
