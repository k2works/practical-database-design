package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.quality.ProcessInspection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工程検査データ Mapper.
 */
@Mapper
public interface ProcessInspectionMapper {
    void insert(ProcessInspection inspection);

    ProcessInspection findById(Integer id);

    ProcessInspection findByInspectionNumber(String inspectionNumber);

    ProcessInspection findByInspectionNumberWithResults(String inspectionNumber);

    List<ProcessInspection> findByWorkOrderNumber(String workOrderNumber);

    List<ProcessInspection> findByProcessCode(String processCode);

    List<ProcessInspection> findAll();

    int update(ProcessInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
