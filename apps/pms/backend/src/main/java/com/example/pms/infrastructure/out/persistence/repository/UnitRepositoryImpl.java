package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.domain.model.unit.Unit;
import com.example.pms.infrastructure.out.persistence.mapper.UnitMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 単位リポジトリ実装.
 */
@Repository
public class UnitRepositoryImpl implements UnitRepository {

    private final UnitMapper unitMapper;

    public UnitRepositoryImpl(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }

    @Override
    public void save(Unit unit) {
        unitMapper.insert(unit);
    }

    @Override
    public Optional<Unit> findByUnitCode(String unitCode) {
        return unitMapper.findByUnitCode(unitCode);
    }

    @Override
    public List<Unit> findAll() {
        return unitMapper.findAll();
    }

    @Override
    public List<Unit> findWithPagination(String keyword, int limit, int offset) {
        return unitMapper.findWithPagination(keyword, limit, offset);
    }

    @Override
    public long count(String keyword) {
        return unitMapper.count(keyword);
    }

    @Override
    public void update(Unit unit) {
        unitMapper.update(unit);
    }

    @Override
    public void deleteByUnitCode(String unitCode) {
        unitMapper.deleteByUnitCode(unitCode);
    }

    @Override
    public void deleteAll() {
        unitMapper.deleteAll();
    }
}
