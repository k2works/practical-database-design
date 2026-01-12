package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ProcessInspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 工程検査結果リポジトリインターフェース.
 */
public interface ProcessInspectionResultRepository {
    void save(ProcessInspectionResult result);

    Optional<ProcessInspectionResult> findById(Integer id);

    List<ProcessInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ProcessInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
