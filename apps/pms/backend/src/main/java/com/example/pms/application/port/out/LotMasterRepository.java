package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.LotMaster;

import java.util.List;
import java.util.Optional;

/**
 * ロットマスタリポジトリインターフェース.
 */
public interface LotMasterRepository {
    void save(LotMaster lot);

    Optional<LotMaster> findById(Integer id);

    Optional<LotMaster> findByLotNumber(String lotNumber);

    Optional<LotMaster> findByLotNumberWithCompositions(String lotNumber);

    List<LotMaster> findByItemCode(String itemCode);

    List<LotMaster> findAll();

    List<LotMaster> traceForward(String lotNumber);

    List<LotMaster> traceBack(String lotNumber);

    int update(LotMaster lot);

    void deleteByLotNumber(String lotNumber);

    void deleteAll();
}
