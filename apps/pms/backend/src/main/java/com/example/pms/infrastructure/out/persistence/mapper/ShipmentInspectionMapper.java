package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ShipmentInspection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 出荷検査データ Mapper.
 */
@Mapper
public interface ShipmentInspectionMapper {
    void insert(ShipmentInspection inspection);

    ShipmentInspection findById(Integer id);

    ShipmentInspection findByInspectionNumber(String inspectionNumber);

    ShipmentInspection findByInspectionNumberWithResults(String inspectionNumber);

    List<ShipmentInspection> findByShipmentNumber(String shipmentNumber);

    List<ShipmentInspection> findAll();

    int update(ShipmentInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
