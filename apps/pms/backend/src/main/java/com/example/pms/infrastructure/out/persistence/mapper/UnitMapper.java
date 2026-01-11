package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.unit.Unit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UnitMapper {
    void insert(Unit unit);
    Optional<Unit> findByUnitCode(String unitCode);
    List<Unit> findAll();
    void update(Unit unit);
    void deleteByUnitCode(String unitCode);
    void deleteAll();
}
