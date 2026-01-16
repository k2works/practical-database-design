package com.example.pms.application.port.out;

import com.example.pms.domain.model.subcontract.Supply;

import java.util.List;
import java.util.Optional;

/**
 * 支給データリポジトリ（Output Port）
 */
public interface SupplyRepository {

    void save(Supply supply);

    Optional<Supply> findById(Integer id);

    Optional<Supply> findBySupplyNumber(String supplyNumber);

    /**
     * 支給番号で検索（明細を含む）.
     *
     * @param supplyNumber 支給番号
     * @return 明細を含む支給データ
     */
    Optional<Supply> findBySupplyNumberWithDetails(String supplyNumber);

    List<Supply> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<Supply> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber);

    List<Supply> findBySupplierCode(String supplierCode);

    List<Supply> findAll();

    void deleteAll();
}
