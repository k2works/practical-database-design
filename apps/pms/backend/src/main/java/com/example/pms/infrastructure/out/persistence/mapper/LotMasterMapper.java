package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.LotMaster;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ロットマスタ Mapper.
 */
@Mapper
public interface LotMasterMapper {
    void insert(LotMaster lot);

    LotMaster findById(Integer id);

    LotMaster findByLotNumber(String lotNumber);

    LotMaster findByLotNumberWithCompositions(String lotNumber);

    List<LotMaster> findByItemCode(String itemCode);

    List<LotMaster> findAll();

    List<LotMaster> traceForward(String lotNumber);

    List<LotMaster> traceBack(String lotNumber);

    int update(LotMaster lot);

    void deleteByLotNumber(String lotNumber);

    void deleteAll();
}
