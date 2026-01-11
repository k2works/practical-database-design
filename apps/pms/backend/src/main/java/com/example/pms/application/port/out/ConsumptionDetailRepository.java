package com.example.pms.application.port.out;

import com.example.pms.domain.model.subcontract.ConsumptionDetail;

import java.util.List;
import java.util.Optional;

/**
 * 消費明細データリポジトリ（Output Port）
 */
public interface ConsumptionDetailRepository {

    void save(ConsumptionDetail consumptionDetail);

    Optional<ConsumptionDetail> findById(Integer id);

    Optional<ConsumptionDetail> findByConsumptionNumberAndLineNumber(
            String consumptionNumber, Integer lineNumber);

    List<ConsumptionDetail> findByConsumptionNumber(String consumptionNumber);

    List<ConsumptionDetail> findAll();

    void deleteAll();
}
