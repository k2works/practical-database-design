package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ShipmentInspection;

import java.util.List;
import java.util.Optional;

/**
 * 出荷検査リポジトリインターフェース.
 */
public interface ShipmentInspectionRepository {
    void save(ShipmentInspection inspection);

    Optional<ShipmentInspection> findById(Integer id);

    Optional<ShipmentInspection> findByInspectionNumber(String inspectionNumber);

    Optional<ShipmentInspection> findByInspectionNumberWithResults(String inspectionNumber);

    List<ShipmentInspection> findByShipmentNumber(String shipmentNumber);

    List<ShipmentInspection> findAll();

    int update(ShipmentInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
