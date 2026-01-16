package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ReceivingInspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査結果リポジトリインターフェース.
 */
public interface ReceivingInspectionResultRepository {
    void save(ReceivingInspectionResult result);

    Optional<ReceivingInspectionResult> findById(Integer id);

    List<ReceivingInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ReceivingInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
