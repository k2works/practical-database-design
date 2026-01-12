package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ShipmentInspectionResultRepository;
import com.example.pms.domain.model.quality.ShipmentInspectionResult;
import com.example.pms.infrastructure.out.persistence.mapper.ShipmentInspectionResultMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 出荷検査結果リポジトリ実装.
 */
@Repository
public class ShipmentInspectionResultRepositoryImpl implements ShipmentInspectionResultRepository {

    private final ShipmentInspectionResultMapper shipmentInspectionResultMapper;

    public ShipmentInspectionResultRepositoryImpl(ShipmentInspectionResultMapper shipmentInspectionResultMapper) {
        this.shipmentInspectionResultMapper = shipmentInspectionResultMapper;
    }

    @Override
    public void save(ShipmentInspectionResult result) {
        shipmentInspectionResultMapper.insert(result);
    }

    @Override
    public Optional<ShipmentInspectionResult> findById(Integer id) {
        return Optional.ofNullable(shipmentInspectionResultMapper.findById(id));
    }

    @Override
    public List<ShipmentInspectionResult> findByInspectionNumber(String inspectionNumber) {
        return shipmentInspectionResultMapper.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public void update(ShipmentInspectionResult result) {
        shipmentInspectionResultMapper.update(result);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        shipmentInspectionResultMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteById(Integer id) {
        shipmentInspectionResultMapper.deleteById(id);
    }
}
