package com.example.pms.application.service;

import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.domain.exception.InvalidOrderStateException;
import com.example.pms.domain.exception.PurchaseOrderNotFoundException;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 発注サービス（Application Service）.
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
    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder getOrder(String orderNumber) {
        return purchaseOrderRepository.findByPurchaseOrderNumberWithDetails(orderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(orderNumber));
    }

    @Override
    public PurchaseOrder createOrder(CreatePurchaseOrderCommand command) {
        String orderNumber = generateOrderNumber();

        List<PurchaseOrderDetail> details = command.getDetails().stream()
            .map(d -> createDetail(orderNumber, d))
            .toList();

        PurchaseOrder order = PurchaseOrder.builder()
            .purchaseOrderNumber(orderNumber)
            .orderDate(LocalDate.now())
            .supplierCode(command.getSupplierCode())
            .ordererCode(command.getOrdererCode())
            .departmentCode(command.getDepartmentCode())
            .status(PurchaseOrderStatus.CREATING)
            .remarks(command.getRemarks())
            .details(details)
            .build();

        purchaseOrderRepository.save(order);
        return order;
    }

    private PurchaseOrderDetail createDetail(
            String orderNumber,
            CreatePurchaseOrderCommand.PurchaseOrderDetailCommand d) {
        BigDecimal orderAmount = d.getOrderQuantity()
            .multiply(d.getOrderUnitPrice() != null ? d.getOrderUnitPrice() : BigDecimal.ZERO);

        return PurchaseOrderDetail.builder()
            .purchaseOrderNumber(orderNumber)
            .itemCode(d.getItemCode())
            .deliveryLocationCode(d.getDeliveryLocationCode())
            .expectedReceivingDate(d.getExpectedReceivingDate())
            .orderQuantity(d.getOrderQuantity())
            .orderUnitPrice(d.getOrderUnitPrice())
            .orderAmount(orderAmount)
            .receivedQuantity(BigDecimal.ZERO)
            .inspectedQuantity(BigDecimal.ZERO)
            .acceptedQuantity(BigDecimal.ZERO)
            .completedFlag(false)
            .detailRemarks(d.getDetailRemarks())
            .build();
    }

    @Override
    public PurchaseOrder confirmOrder(String orderNumber) {
        PurchaseOrder order = purchaseOrderRepository.findByPurchaseOrderNumber(orderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(orderNumber));

        if (order.getStatus() != PurchaseOrderStatus.CREATING) {
            throw new InvalidOrderStateException(
                orderNumber,
                order.getStatus().getDisplayName(),
                PurchaseOrderStatus.CREATING.getDisplayName());
        }

        purchaseOrderRepository.updateStatus(order.getId(), PurchaseOrderStatus.ORDERED);

        return purchaseOrderRepository.findByPurchaseOrderNumber(orderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(orderNumber));
    }

    @Override
    public void cancelOrder(String orderNumber) {
        PurchaseOrder order = purchaseOrderRepository.findByPurchaseOrderNumber(orderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(orderNumber));

        if (order.getStatus() != PurchaseOrderStatus.CREATING
                && order.getStatus() != PurchaseOrderStatus.ORDERED) {
            throw new InvalidOrderStateException(
                orderNumber,
                order.getStatus().getDisplayName(),
                "作成中 or 発注済");
        }

        purchaseOrderRepository.updateStatus(order.getId(), PurchaseOrderStatus.CANCELLED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrder> getOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    private String generateOrderNumber() {
        return "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
    }
}
