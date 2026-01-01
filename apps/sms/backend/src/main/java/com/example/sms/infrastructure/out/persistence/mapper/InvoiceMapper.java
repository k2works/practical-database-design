package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceDetail;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 請求マッパー.
 */
@Mapper
public interface InvoiceMapper {

    void insertHeader(Invoice invoice);

    void insertDetail(InvoiceDetail detail);

    Optional<Invoice> findById(Integer id);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerCode(String customerCode);

    List<Invoice> findByStatus(@Param("status") InvoiceStatus status);

    List<Invoice> findByInvoiceDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Invoice> findUnpaidByCustomerCode(String customerCode);

    Optional<Invoice> findLatestByCustomerCode(String customerCode);

    List<Invoice> findAll();

    List<InvoiceDetail> findDetailsByInvoiceId(Integer invoiceId);

    Invoice findWithDetailsByInvoiceNumber(String invoiceNumber);

    Invoice findByIdWithDetails(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(Invoice invoice);

    int updateWithOptimisticLock(Invoice invoice);

    void updateReceiptAmount(@Param("invoiceId") Integer invoiceId, @Param("amount") BigDecimal amount);

    void updateStatus(@Param("invoiceId") Integer invoiceId, @Param("status") InvoiceStatus status);

    void deleteDetailsByInvoiceId(Integer invoiceId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
