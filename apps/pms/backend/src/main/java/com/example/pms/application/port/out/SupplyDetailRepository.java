package com.example.pms.application.port.out;

import com.example.pms.domain.model.subcontract.SupplyDetail;

import java.util.List;
import java.util.Optional;

/**
 * 支給明細データリポジトリ（Output Port）
 */
public interface SupplyDetailRepository {

    void save(SupplyDetail supplyDetail);

    Optional<SupplyDetail> findById(Integer id);

    Optional<SupplyDetail> findBySupplyNumberAndLineNumber(
            String supplyNumber, Integer lineNumber);

    List<SupplyDetail> findBySupplyNumber(String supplyNumber);

    List<SupplyDetail> findAll();

    void deleteAll();
}
