package com.example.sms.application.service;

import com.example.sms.application.port.in.PurchaseOrderUseCase;
import com.example.sms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.domain.exception.PurchaseOrderNotFoundException;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 発注アプリケーションサービス.
 */
@Service
@Transactional
public class PurchaseOrderService implements PurchaseOrderUseCase {

    private static final DateTimeFormatter ORDER_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public PurchaseOrder createPurchaseOrder(CreatePurchaseOrderCommand command) {
        String purchaseOrderNumber = generatePurchaseOrderNumber();

        List<PurchaseOrderDetail> details = new ArrayList<>();
        int lineNumber = 1;
        for (CreatePurchaseOrderCommand.CreatePurchaseOrderDetailCommand detailCmd : command.details()) {
            PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                .lineNumber(lineNumber++)
                .productCode(detailCmd.productCode())
                .orderQuantity(detailCmd.orderQuantity())
                .unitPrice(detailCmd.unitPrice())
                .expectedDeliveryDate(detailCmd.expectedDeliveryDate())
                .receivedQuantity(BigDecimal.ZERO)
                .remarks(detailCmd.remarks())
                .build();
            detail.calculateOrderAmount();
            detail.calculateRemainingQuantity();
            details.add(detail);
        }

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
            .purchaseOrderNumber(purchaseOrderNumber)
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(command.supplierBranchNumber() != null ? command.supplierBranchNumber() : "00")
            .orderDate(command.orderDate() != null ? command.orderDate() : LocalDate.now())
            .desiredDeliveryDate(command.desiredDeliveryDate())
            .status(PurchaseOrderStatus.DRAFT)
            .purchaserCode(command.purchaserCode())
            .remarks(command.remarks())
            .details(details)
            .build();

        purchaseOrder.recalculateTotalAmount();
        purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrder;
    }

    private String generatePurchaseOrderNumber() {
        String datePrefix = LocalDate.now().format(ORDER_NUMBER_FORMAT);
        List<PurchaseOrder> todayOrders = purchaseOrderRepository.findByOrderDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayOrders.size() + 1;
        return String.format("PO-%s-%04d", datePrefix, sequence);
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
