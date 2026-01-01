package com.example.sms.application.port.out;

import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 請求リポジトリ（Output Port）.
 */
public interface InvoiceRepository {

    void save(Invoice invoice);

    Optional<Invoice> findById(Integer id);

    Optional<Invoice> findByIdWithDetails(Integer id);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findWithDetailsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerCode(String customerCode);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByInvoiceDateBetween(LocalDate from, LocalDate to);

    List<Invoice> findUnpaidByCustomerCode(String customerCode);

    Optional<Invoice> findLatestByCustomerCode(String customerCode);

    List<Invoice> findAll();

    void update(Invoice invoice);

    void deleteById(Integer id);

    void deleteAll();
}
