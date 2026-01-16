package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.WageRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 賃率マスタ Mapper.
 */
@Mapper
public interface WageRateMapper {

    void insert(WageRate wageRate);

    int update(WageRate wageRate);

    WageRate findById(Integer id);

    List<WageRate> findByWorkerCategoryCode(String workerCategoryCode);

    WageRate findValidByWorkerCategoryCode(
            @Param("workerCategoryCode") String workerCategoryCode,
            @Param("targetDate") LocalDate targetDate);

    List<WageRate> findAll();

    void deleteById(Integer id);

    void deleteAll();
}
