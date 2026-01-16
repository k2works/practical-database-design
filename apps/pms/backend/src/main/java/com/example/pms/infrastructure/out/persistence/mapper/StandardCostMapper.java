package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.cost.StandardCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 標準原価マスタ Mapper.
 */
@Mapper
public interface StandardCostMapper {

    void insert(StandardCost standardCost);

    int update(StandardCost standardCost);

    StandardCost findById(Integer id);

    List<StandardCost> findByItemCode(String itemCode);

    StandardCost findValidByItemCode(
            @Param("itemCode") String itemCode,
            @Param("targetDate") LocalDate targetDate);

    List<StandardCost> findAll();

    void deleteById(Integer id);

    void deleteByItemCode(String itemCode);

    void deleteAll();
}
