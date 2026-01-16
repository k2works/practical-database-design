package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ReceivingInspection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 受入検査データ Mapper.
 */
@Mapper
public interface ReceivingInspectionMapper {
    void insert(ReceivingInspection inspection);

    ReceivingInspection findById(Integer id);

    ReceivingInspection findByInspectionNumber(String inspectionNumber);

    ReceivingInspection findByInspectionNumberWithResults(String inspectionNumber);

    List<ReceivingInspection> findByReceivingNumber(String receivingNumber);

    List<ReceivingInspection> findBySupplierCode(String supplierCode);

    List<ReceivingInspection> findAll();

    int update(ReceivingInspection inspection);

    int updateJudgment(ReceivingInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
