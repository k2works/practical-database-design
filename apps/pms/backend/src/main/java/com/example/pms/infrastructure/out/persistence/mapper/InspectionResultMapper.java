package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.InspectionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 完成検査結果 Mapper.
 */
@Mapper
public interface InspectionResultMapper {

    void insert(InspectionResult inspectionResult);

    void update(InspectionResult inspectionResult);

    InspectionResult findById(Integer id);

    InspectionResult findByCompletionResultNumberAndDefectCode(
            @Param("completionResultNumber") String completionResultNumber,
            @Param("defectCode") String defectCode);

    List<InspectionResult> findByCompletionResultNumber(String completionResultNumber);

    List<InspectionResult> findAll();

    List<InspectionResult> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    void deleteById(Integer id);

    void deleteAll();
}
