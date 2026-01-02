package com.example.sms.application.service;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.command.CreateOrderCommand;
import com.example.sms.application.port.in.command.UpdateOrderCommand;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.SalesOrderNotFoundException;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 受注アプリケーションサービス.
 */
@Service
@Transactional
public class OrderService implements OrderUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final DateTimeFormatter ORDER_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final SalesOrderRepository salesOrderRepository;

    public OrderService(SalesOrderRepository salesOrderRepository) {
        this.salesOrderRepository = salesOrderRepository;
    }

    @Override
    public SalesOrder createOrder(CreateOrderCommand command) {
        String orderNumber = generateOrderNumber();

        List<SalesOrderDetail> details = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (command.details() != null) {
            for (CreateOrderCommand.CreateOrderDetailCommand detailCmd : command.details()) {
                BigDecimal amount = detailCmd.unitPrice().multiply(detailCmd.orderQuantity());
                BigDecimal detailTax = amount.multiply(TAX_RATE).setScale(0, RoundingMode.DOWN);

                SalesOrderDetail detail = SalesOrderDetail.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .productCode(detailCmd.productCode())
                    .productName(detailCmd.productName())
                    .orderQuantity(detailCmd.orderQuantity())
                    .remainingQuantity(detailCmd.orderQuantity())
                    .unit(detailCmd.unit())
                    .unitPrice(detailCmd.unitPrice())
                    .amount(amount)
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .taxRate(TAX_RATE_PERCENT)
                    .taxAmount(detailTax)
                    .warehouseCode(detailCmd.warehouseCode())
                    .requestedDeliveryDate(detailCmd.requestedDeliveryDate())
                    .remarks(detailCmd.remarks())
                    .build();

                details.add(detail);
                orderAmount = orderAmount.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }
        }

        SalesOrder salesOrder = SalesOrder.builder()
            .orderNumber(orderNumber)
            .orderDate(command.orderDate() != null ? command.orderDate() : LocalDate.now())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .shippingDestinationNumber(command.shippingDestinationNumber())
            .representativeCode(command.representativeCode())
            .requestedDeliveryDate(command.requestedDeliveryDate())
            .scheduledShippingDate(command.scheduledShippingDate())
            .orderAmount(orderAmount)
            .taxAmount(taxAmount)
            .totalAmount(orderAmount.add(taxAmount))
            .status(OrderStatus.RECEIVED)
            .quotationId(command.quotationId())
            .customerOrderNumber(command.customerOrderNumber())
            .remarks(command.remarks())
            .details(details)
            .build();

        salesOrderRepository.save(salesOrder);
        return salesOrder;
    }

    @Override
    public SalesOrder updateOrder(String orderNumber, UpdateOrderCommand command) {
        SalesOrder existing = salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        validateOptimisticLock(command.version(), existing.getVersion(), orderNumber);

        SalesOrder updated = buildUpdatedOrder(existing, command, orderNumber);

        salesOrderRepository.update(updated);
        return updated;
    }

    private void validateOptimisticLock(Integer commandVersion, Integer existingVersion, String orderNumber) {
        if (commandVersion != null && !commandVersion.equals(existingVersion)) {
            throw new OptimisticLockException("受注", orderNumber);
        }
    }

    private SalesOrder buildUpdatedOrder(SalesOrder existing, UpdateOrderCommand command, String orderNumber) {
        return SalesOrder.builder()
            .id(existing.getId())
            .orderNumber(orderNumber)
            .orderDate(existing.getOrderDate())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(coalesce(command.shippingDestinationNumber(),
                existing.getShippingDestinationNumber()))
            .representativeCode(coalesce(command.representativeCode(), existing.getRepresentativeCode()))
            .requestedDeliveryDate(coalesce(command.requestedDeliveryDate(), existing.getRequestedDeliveryDate()))
            .scheduledShippingDate(coalesce(command.scheduledShippingDate(), existing.getScheduledShippingDate()))
            .orderAmount(existing.getOrderAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(coalesce(command.status(), existing.getStatus()))
            .quotationId(existing.getQuotationId())
            .customerOrderNumber(coalesce(command.customerOrderNumber(), existing.getCustomerOrderNumber()))
            .remarks(coalesce(command.remarks(), existing.getRemarks()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getAllOrders() {
        return salesOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrder getOrderByNumber(String orderNumber) {
        return salesOrderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrder getOrderWithDetails(String orderNumber) {
        return salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getOrdersByStatus(OrderStatus status) {
        return salesOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getOrdersByCustomer(String customerCode) {
        return salesOrderRepository.findByCustomerCode(customerCode);
    }

    @Override
    public void deleteOrder(String orderNumber) {
        SalesOrder existing = salesOrderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        salesOrderRepository.deleteById(existing.getId());
    }

    @Override
    public SalesOrder cancelOrder(String orderNumber, Integer version) {
        SalesOrder existing = salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        if (version != null && !version.equals(existing.getVersion())) {
            throw new OptimisticLockException("受注", orderNumber);
        }

        SalesOrder cancelled = SalesOrder.builder()
            .id(existing.getId())
            .orderNumber(orderNumber)
            .orderDate(existing.getOrderDate())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(existing.getShippingDestinationNumber())
            .representativeCode(existing.getRepresentativeCode())
            .requestedDeliveryDate(existing.getRequestedDeliveryDate())
            .scheduledShippingDate(existing.getScheduledShippingDate())
            .orderAmount(existing.getOrderAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(OrderStatus.CANCELLED)
            .quotationId(existing.getQuotationId())
            .customerOrderNumber(existing.getCustomerOrderNumber())
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        salesOrderRepository.update(cancelled);
        return cancelled;
    }

    private String generateOrderNumber() {
        String datePrefix = LocalDate.now().format(ORDER_NUMBER_FORMAT);
        List<SalesOrder> todayOrders = salesOrderRepository.findByOrderDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayOrders.size() + 1;
        return String.format("ORD-%s-%04d", datePrefix, sequence);
    }
}
