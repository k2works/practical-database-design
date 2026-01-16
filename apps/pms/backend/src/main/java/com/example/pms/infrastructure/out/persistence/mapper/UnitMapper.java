package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.unit.Unit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UnitMapper {
    void insert(Unit unit);
    Optional<Unit> findByUnitCode(String unitCode);
    List<Unit> findAll();
    List<Unit> findWithPagination(@Param("keyword") String keyword,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);
    long count(@Param("keyword") String keyword);
    void update(Unit unit);
    void deleteByUnitCode(String unitCode);
    void deleteAll();
}
