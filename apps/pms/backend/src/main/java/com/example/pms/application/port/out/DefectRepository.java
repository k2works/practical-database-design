package com.example.pms.application.port.out;

import com.example.pms.domain.model.defect.Defect;

import java.util.List;
import java.util.Optional;

/**
 * 欠点マスタリポジトリインターフェース.
 */
public interface DefectRepository {
    void save(Defect defect);
    Optional<Defect> findByDefectCode(String defectCode);
    List<Defect> findAll();
    void update(Defect defect);
    void deleteByDefectCode(String defectCode);
    void deleteAll();
}
