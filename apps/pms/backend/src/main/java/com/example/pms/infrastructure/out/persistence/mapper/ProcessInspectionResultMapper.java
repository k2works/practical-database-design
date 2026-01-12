package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ProcessInspectionResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工程検査結果データ Mapper.
 */
@Mapper
public interface ProcessInspectionResultMapper {
    void insert(ProcessInspectionResult result);

    ProcessInspectionResult findById(Integer id);

    List<ProcessInspectionResult> findByInspectionNumber(String inspectionNumber);

    void update(ProcessInspectionResult result);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteById(Integer id);
}
