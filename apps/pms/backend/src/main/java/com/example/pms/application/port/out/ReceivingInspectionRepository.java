package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ReceivingInspection;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査リポジトリインターフェース.
 */
public interface ReceivingInspectionRepository {
    void save(ReceivingInspection inspection);

    Optional<ReceivingInspection> findById(Integer id);

    Optional<ReceivingInspection> findByInspectionNumber(String inspectionNumber);

    Optional<ReceivingInspection> findByInspectionNumberWithResults(String inspectionNumber);

    List<ReceivingInspection> findByReceivingNumber(String receivingNumber);

    List<ReceivingInspection> findBySupplierCode(String supplierCode);

    List<ReceivingInspection> findAll();

    int update(ReceivingInspection inspection);

    int updateJudgment(ReceivingInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
