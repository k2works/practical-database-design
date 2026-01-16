package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.WageRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 賃率マスタリポジトリ.
 */
public interface WageRateRepository {

    void save(WageRate wageRate);

    int update(WageRate wageRate);

    Optional<WageRate> findById(Integer id);

    List<WageRate> findByWorkerCategoryCode(String workerCategoryCode);

    Optional<WageRate> findValidByWorkerCategoryCode(String workerCategoryCode, LocalDate targetDate);

    List<WageRate> findAll();

    void deleteById(Integer id);

    void deleteAll();
}
