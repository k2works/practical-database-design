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

    /**
     * 受入検査番号で検索（検収を含む）.
     *
     * @param inspectionNumber 受入検査番号
     * @return 検収を含む検査データ
     */
    Optional<Inspection> findByInspectionNumberWithAcceptances(String inspectionNumber);

    List<Inspection> findByReceivingNumber(String receivingNumber);

    List<Inspection> findAll();

    void deleteAll();
}
