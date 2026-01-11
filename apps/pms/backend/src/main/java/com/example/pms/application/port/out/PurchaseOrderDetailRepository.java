package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.PurchaseOrderDetail;

import java.util.List;
import java.util.Optional;

/**
 * 発注明細データリポジトリ（Output Port）
 */
public interface PurchaseOrderDetailRepository {

    void save(PurchaseOrderDetail detail);

    Optional<PurchaseOrderDetail> findById(Integer id);

    List<PurchaseOrderDetail> findByPurchaseOrderNumber(String purchaseOrderNumber);

    Optional<PurchaseOrderDetail> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber);

    List<PurchaseOrderDetail> findAll();

    void deleteAll();
}
