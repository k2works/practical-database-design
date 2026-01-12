package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ShipmentInspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 出荷検査結果リポジトリインターフェース.
 */
public interface ShipmentInspectionResultRepository {
    void save(ShipmentInspectionResult result);

    Optional<ShipmentInspectionResult> findById(Integer id);

    List<ShipmentInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ShipmentInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
