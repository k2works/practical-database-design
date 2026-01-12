package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WageRateRepository;
import com.example.pms.domain.model.cost.WageRate;
import com.example.pms.infrastructure.out.persistence.mapper.WageRateMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 賃率マスタリポジトリ実装.
 */
@Repository
public class WageRateRepositoryImpl implements WageRateRepository {

    private final WageRateMapper mapper;

    public WageRateRepositoryImpl(WageRateMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(WageRate wageRate) {
        mapper.insert(wageRate);
    }

    @Override
    public int update(WageRate wageRate) {
        return mapper.update(wageRate);
    }

    @Override
    public Optional<WageRate> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<WageRate> findByWorkerCategoryCode(String workerCategoryCode) {
        return mapper.findByWorkerCategoryCode(workerCategoryCode);
    }

    @Override
    public Optional<WageRate> findValidByWorkerCategoryCode(String workerCategoryCode, LocalDate targetDate) {
        return Optional.ofNullable(mapper.findValidByWorkerCategoryCode(workerCategoryCode, targetDate));
    }

    @Override
    public List<WageRate> findAll() {
        return mapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        mapper.deleteAll();
    }
}
