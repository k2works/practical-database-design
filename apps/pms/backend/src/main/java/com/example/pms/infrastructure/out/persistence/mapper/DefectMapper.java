package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.defect.Defect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DefectMapper {
    void insert(Defect defect);
    Optional<Defect> findByDefectCode(String defectCode);
    List<Defect> findAll();
    List<Defect> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);
    long count(@Param("keyword") String keyword);
    void update(Defect defect);
    void deleteByDefectCode(String defectCode);
    void deleteAll();
}
