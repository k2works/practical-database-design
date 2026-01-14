package com.example.pms.application.port.in;

import com.example.pms.domain.model.purchase.Inspection;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査ユースケース（Input Port）.
 */
public interface InspectionUseCase {

    /**
     * 全受入検査を取得する.
     *
     * @return 受入検査リスト
     */
    List<Inspection> getAllInspections();

    /**
     * 受入検査を取得する.
     *
     * @param inspectionNumber 受入検査番号
     * @return 受入検査
     */
    Optional<Inspection> getInspection(String inspectionNumber);
}
