package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.LotComposition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ロット構成 Mapper.
 */
@Mapper
public interface LotCompositionMapper {
    void insert(LotComposition composition);

    LotComposition findById(Integer id);

    List<LotComposition> findByParentLotNumber(String parentLotNumber);

    List<LotComposition> findByChildLotNumber(String childLotNumber);

    List<LotComposition> findAll();

    void update(LotComposition composition);

    void deleteById(Integer id);

    void deleteByParentLotNumber(String parentLotNumber);

    void deleteByChildLotNumber(String childLotNumber);
}
