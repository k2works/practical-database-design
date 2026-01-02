package com.example.sms.application.port.out;

import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 発注リポジトリ（Output Port）.
 */
public interface PurchaseOrderRepository {

    void save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findById(Integer id);

    Optional<PurchaseOrder> findByIdWithDetails(Integer id);

    Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber);

    Optional<PurchaseOrder> findWithDetailsByPurchaseOrderNumber(String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplierCode(String supplierCode);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    List<PurchaseOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

    List<PurchaseOrder> findByDesiredDeliveryDateBetween(LocalDate from, LocalDate to);

    List<PurchaseOrder> findAll();

    void update(PurchaseOrder purchaseOrder);

    void deleteById(Integer id);

    void deleteAll();
}
