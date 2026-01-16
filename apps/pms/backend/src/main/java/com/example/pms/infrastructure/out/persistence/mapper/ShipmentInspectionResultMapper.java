package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ShipmentInspectionResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 出荷検査結果データ Mapper.
 */
@Mapper
public interface ShipmentInspectionResultMapper {
    void insert(ShipmentInspectionResult result);

    ShipmentInspectionResult findById(Integer id);

    List<ShipmentInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ShipmentInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
