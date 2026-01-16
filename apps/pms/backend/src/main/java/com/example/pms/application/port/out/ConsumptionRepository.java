package com.example.pms.application.port.out;

import com.example.pms.domain.model.subcontract.Consumption;

import java.util.List;
import java.util.Optional;

/**
 * 消費データリポジトリ（Output Port）
 */
public interface ConsumptionRepository {

    void save(Consumption consumption);

    Optional<Consumption> findById(Integer id);

    Optional<Consumption> findByConsumptionNumber(String consumptionNumber);

    /**
     * 消費番号で検索（明細を含む）.
     *
     * @param consumptionNumber 消費番号
     * @return 明細を含む消費データ
     */
    Optional<Consumption> findByConsumptionNumberWithDetails(String consumptionNumber);

    List<Consumption> findByReceivingNumber(String receivingNumber);

    List<Consumption> findBySupplierCode(String supplierCode);

    List<Consumption> findAll();

    void deleteAll();
}
