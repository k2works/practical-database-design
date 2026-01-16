package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ReceivingInspectionResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 受入検査結果データ Mapper.
 */
@Mapper
public interface ReceivingInspectionResultMapper {
    void insert(ReceivingInspectionResult result);

    ReceivingInspectionResult findById(Integer id);

    List<ReceivingInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ReceivingInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
