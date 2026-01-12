package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ShipmentInspectionRepository;
import com.example.pms.domain.model.quality.ShipmentInspection;
import com.example.pms.infrastructure.out.persistence.mapper.ShipmentInspectionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 出荷検査リポジトリ実装.
 */
@Repository
public class ShipmentInspectionRepositoryImpl implements ShipmentInspectionRepository {

    private final ShipmentInspectionMapper shipmentInspectionMapper;

    public ShipmentInspectionRepositoryImpl(ShipmentInspectionMapper shipmentInspectionMapper) {
        this.shipmentInspectionMapper = shipmentInspectionMapper;
    }

    @Override
    public void save(ShipmentInspection inspection) {
        shipmentInspectionMapper.insert(inspection);
    }

    @Override
    public Optional<ShipmentInspection> findById(Integer id) {
        return Optional.ofNullable(shipmentInspectionMapper.findById(id));
    }

    @Override
    public Optional<ShipmentInspection> findByInspectionNumber(String inspectionNumber) {
        return Optional.ofNullable(shipmentInspectionMapper.findByInspectionNumber(inspectionNumber));
    }

    @Override
    public Optional<ShipmentInspection> findByInspectionNumberWithResults(String inspectionNumber) {
        return Optional.ofNullable(shipmentInspectionMapper.findByInspectionNumberWithResults(inspectionNumber));
    }

    @Override
    public List<ShipmentInspection> findByShipmentNumber(String shipmentNumber) {
        return shipmentInspectionMapper.findByShipmentNumber(shipmentNumber);
    }

    @Override
    public List<ShipmentInspection> findAll() {
        return shipmentInspectionMapper.findAll();
    }

    @Override
    public int update(ShipmentInspection inspection) {
        return shipmentInspectionMapper.update(inspection);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        shipmentInspectionMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteAll() {
        shipmentInspectionMapper.deleteAll();
    }
}
