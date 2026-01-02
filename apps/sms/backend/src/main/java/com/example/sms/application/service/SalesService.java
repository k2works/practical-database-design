package com.example.sms.application.service;

import com.example.sms.application.port.in.SalesUseCase;
import com.example.sms.application.port.in.command.CreateSalesCommand;
import com.example.sms.application.port.in.command.UpdateSalesCommand;
import com.example.sms.application.port.out.SalesRepository;
import com.example.sms.domain.exception.SalesNotFoundException;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesDetail;
import com.example.sms.domain.model.sales.SalesStatus;
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
 * 売上アプリケーションサービス.
 */
@Service
@Transactional
public class SalesService implements SalesUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final DateTimeFormatter SALES_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Override
    public Sales createSales(CreateSalesCommand command) {
        String salesNumber = generateSalesNumber();

        List<SalesDetail> details = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        BigDecimal salesAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (command.details() != null) {
            for (CreateSalesCommand.CreateSalesDetailCommand detailCmd : command.details()) {
                BigDecimal amount = detailCmd.unitPrice().multiply(detailCmd.salesQuantity());
                BigDecimal detailTax = amount.multiply(TAX_RATE).setScale(0, RoundingMode.DOWN);

                SalesDetail detail = SalesDetail.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .orderDetailId(detailCmd.orderDetailId())
                    .shipmentDetailId(detailCmd.shipmentDetailId())
                    .productCode(detailCmd.productCode())
                    .productName(detailCmd.productName())
                    .salesQuantity(detailCmd.salesQuantity())
                    .unit(detailCmd.unit())
                    .unitPrice(detailCmd.unitPrice())
                    .amount(amount)
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .taxRate(TAX_RATE_PERCENT)
                    .taxAmount(detailTax)
                    .remarks(detailCmd.remarks())
                    .build();

                details.add(detail);
                salesAmount = salesAmount.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }
        }

        Sales sales = Sales.builder()
            .salesNumber(salesNumber)
            .salesDate(command.salesDate() != null ? command.salesDate() : LocalDate.now())
            .orderId(command.orderId())
            .shipmentId(command.shipmentId())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .representativeCode(command.representativeCode())
            .salesAmount(salesAmount)
            .taxAmount(taxAmount)
            .totalAmount(salesAmount.add(taxAmount))
            .status(SalesStatus.RECORDED)
            .remarks(command.remarks())
            .details(details)
            .build();

        salesRepository.save(sales);
        return sales;
    }

    @Override
    public Sales updateSales(String salesNumber, UpdateSalesCommand command) {
        Sales existing = salesRepository.findBySalesNumber(salesNumber)
            .orElseThrow(() -> new SalesNotFoundException(salesNumber));

        Sales updated = Sales.builder()
            .id(existing.getId())
            .salesNumber(salesNumber)
            .salesDate(existing.getSalesDate())
            .orderId(existing.getOrderId())
            .shipmentId(existing.getShipmentId())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .representativeCode(existing.getRepresentativeCode())
            .salesAmount(existing.getSalesAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(command.status() != null ? command.status() : existing.getStatus())
            .billingId(command.billingId() != null ? command.billingId() : existing.getBillingId())
            .remarks(command.remarks() != null ? command.remarks() : existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .details(existing.getDetails())
            .build();

        salesRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Sales getSalesByNumber(String salesNumber) {
        return salesRepository.findBySalesNumber(salesNumber)
            .orElseThrow(() -> new SalesNotFoundException(salesNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sales> getSalesByStatus(SalesStatus status) {
        return salesRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sales> getSalesByCustomer(String customerCode) {
        return salesRepository.findByCustomerCode(customerCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sales> getSalesByOrder(Integer orderId) {
        return salesRepository.findByOrderId(orderId);
    }

    @Override
    public void deleteSales(String salesNumber) {
        Sales existing = salesRepository.findBySalesNumber(salesNumber)
            .orElseThrow(() -> new SalesNotFoundException(salesNumber));

        salesRepository.deleteById(existing.getId());
    }

    @Override
    public Sales cancelSales(String salesNumber) {
        Sales existing = salesRepository.findBySalesNumber(salesNumber)
            .orElseThrow(() -> new SalesNotFoundException(salesNumber));

        Sales cancelled = Sales.builder()
            .id(existing.getId())
            .salesNumber(salesNumber)
            .salesDate(existing.getSalesDate())
            .orderId(existing.getOrderId())
            .shipmentId(existing.getShipmentId())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .representativeCode(existing.getRepresentativeCode())
            .salesAmount(existing.getSalesAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(SalesStatus.CANCELLED)
            .billingId(existing.getBillingId())
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .details(existing.getDetails())
            .build();

        salesRepository.update(cancelled);
        return cancelled;
    }

    private String generateSalesNumber() {
        String datePrefix = LocalDate.now().format(SALES_NUMBER_FORMAT);
        List<Sales> todaySales = salesRepository.findBySalesDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todaySales.size() + 1;
        return String.format("SLS-%s-%04d", datePrefix, sequence);
    }
}
