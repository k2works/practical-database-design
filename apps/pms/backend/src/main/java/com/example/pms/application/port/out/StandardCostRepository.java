package com.example.pms.application.port.out;

import com.example.pms.domain.model.cost.StandardCost;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 標準原価マスタリポジトリ.
 */
public interface StandardCostRepository {

    void save(StandardCost standardCost);

    int update(StandardCost standardCost);

    Optional<StandardCost> findById(Integer id);

    List<StandardCost> findByItemCode(String itemCode);

    Optional<StandardCost> findValidByItemCode(String itemCode, LocalDate targetDate);

    List<StandardCost> findAll();

    void deleteById(Integer id);

    void deleteByItemCode(String itemCode);

    void deleteAll();
}
