package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.InspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 完成検査結果リポジトリ.
 */
public interface InspectionResultRepository {

    void save(InspectionResult inspectionResult);

    Optional<InspectionResult> findById(Integer id);

    Optional<InspectionResult> findByCompletionResultNumberAndDefectCode(
            String completionResultNumber, String defectCode);

    List<InspectionResult> findByCompletionResultNumber(String completionResultNumber);

    List<InspectionResult> findAll();

    void deleteAll();
}
