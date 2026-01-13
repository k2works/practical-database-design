package com.example.pms.application.service;

import com.example.pms.application.port.in.UnitUseCase;
import com.example.pms.application.port.out.UnitRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unit.Unit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 単位サービス（Application Service）.
 */
@Service
@Transactional
public class UnitService implements UnitUseCase {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Unit> getUnits(int page, int size, String keyword) {
        int offset = page * size;
        List<Unit> units = unitRepository.findWithPagination(keyword, size, offset);
        long totalElements = unitRepository.count(keyword);
        return new PageResult<>(units, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    @Override
    public Unit createUnit(Unit unit) {
        unitRepository.save(unit);
        return unit;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Unit> getUnit(String unitCode) {
        return unitRepository.findByUnitCode(unitCode);
    }

    @Override
    public Unit updateUnit(String unitCode, Unit unit) {
        unitRepository.update(unit);
        return unit;
    }

    @Override
    public void deleteUnit(String unitCode) {
        unitRepository.deleteByUnitCode(unitCode);
    }
}
