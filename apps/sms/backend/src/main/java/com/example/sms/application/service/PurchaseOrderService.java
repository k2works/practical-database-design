package com.example.sms.application.service;

import com.example.sms.application.port.in.PurchaseOrderUseCase;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.domain.exception.PurchaseOrderNotFoundException;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 発注アプリケーションサービス.
 */
@Service
@Transactional
public class PurchaseOrderService implements PurchaseOrderUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderByNumber(String purchaseOrderNumber) {
        return purchaseOrderRepository.findByPurchaseOrderNumber(purchaseOrderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(purchaseOrderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderWithDetails(String purchaseOrderNumber) {
        return purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber(purchaseOrderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(purchaseOrderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(String supplierCode) {
        return purchaseOrderRepository.findBySupplierCode(supplierCode);
    }
}
