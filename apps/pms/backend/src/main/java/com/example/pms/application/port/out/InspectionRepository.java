package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.Inspection;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査データリポジトリ（Output Port）
 */
public interface InspectionRepository {

    void save(Inspection inspection);

    Optional<Inspection> findById(Integer id);

    Optional<Inspection> findByInspectionNumber(String inspectionNumber);

    List<Inspection> findByReceivingNumber(String receivingNumber);

    List<Inspection> findAll();

    void deleteAll();
}
