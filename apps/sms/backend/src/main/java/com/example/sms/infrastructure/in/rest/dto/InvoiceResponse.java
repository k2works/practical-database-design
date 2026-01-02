package com.example.sms.infrastructure.in.rest.dto;

import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.invoice.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求レスポンス DTO.
 */
public record InvoiceResponse(
    Integer id,
    String invoiceNumber,
    LocalDate invoiceDate,
    String billingCode,
    String customerCode,
    String customerBranchNumber,
    LocalDate closingDate,
    InvoiceType invoiceType,
    BigDecimal previousBalance,
    BigDecimal receiptAmount,
    BigDecimal carriedBalance,
    BigDecimal currentSalesAmount,
    BigDecimal currentTaxAmount,
    BigDecimal currentInvoiceAmount,
    BigDecimal invoiceBalance,
    LocalDate dueDate,
    InvoiceStatus status,
    String remarks,
    Integer version
) {

    /**
     * ドメインモデルからレスポンス DTO を作成.
     *
     * @param invoice 請求ドメインモデル
     * @return 請求レスポンス DTO
     */
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate(),
            invoice.getBillingCode(),
            invoice.getCustomerCode(),
            invoice.getCustomerBranchNumber(),
            invoice.getClosingDate(),
            invoice.getInvoiceType(),
            invoice.getPreviousBalance(),
            invoice.getReceiptAmount(),
            invoice.getCarriedBalance(),
            invoice.getCurrentSalesAmount(),
            invoice.getCurrentTaxAmount(),
            invoice.getCurrentInvoiceAmount(),
            invoice.getInvoiceBalance(),
            invoice.getDueDate(),
            invoice.getStatus(),
            invoice.getRemarks(),
            invoice.getVersion()
        );
    }
}
