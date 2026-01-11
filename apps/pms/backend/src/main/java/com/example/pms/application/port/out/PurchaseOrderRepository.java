package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * 発注データリポジトリ（Output Port）
 */
public interface PurchaseOrderRepository {

    void save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findById(Integer id);

    Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplierCode(String supplierCode);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    List<PurchaseOrder> findAll();

    void updateStatus(Integer id, PurchaseOrderStatus status);

    void deleteAll();
}
