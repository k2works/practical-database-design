package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.LotComposition;

import java.util.List;
import java.util.Optional;

/**
 * ロット構成リポジトリインターフェース.
 */
public interface LotCompositionRepository {
    void save(LotComposition composition);

    Optional<LotComposition> findById(Integer id);

    List<LotComposition> findByParentLotNumber(String parentLotNumber);

    List<LotComposition> findByChildLotNumber(String childLotNumber);

    List<LotComposition> findAll();

    void update(LotComposition composition);

    void deleteById(Integer id);

    void deleteByParentLotNumber(String parentLotNumber);

    void deleteByChildLotNumber(String childLotNumber);
}
