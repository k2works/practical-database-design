package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.InvoiceRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceDetail;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.infrastructure.out.persistence.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 請求リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoiceMapper invoiceMapper;

    @Override
    public void save(Invoice invoice) {
        invoiceMapper.insertHeader(invoice);
        if (invoice.getDetails() != null) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                detail.setInvoiceId(invoice.getId());
                invoiceMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Invoice> findById(Integer id) {
        return invoiceMapper.findById(id);
    }

    @Override
    public Optional<Invoice> findByIdWithDetails(Integer id) {
        return Optional.ofNullable(invoiceMapper.findByIdWithDetails(id));
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return invoiceMapper.findByInvoiceNumber(invoiceNumber);
    }

    @Override
    public Optional<Invoice> findWithDetailsByInvoiceNumber(String invoiceNumber) {
        return Optional.ofNullable(invoiceMapper.findWithDetailsByInvoiceNumber(invoiceNumber));
    }

    @Override
    public List<Invoice> findByCustomerCode(String customerCode) {
        return invoiceMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return invoiceMapper.findByStatus(status);
    }

    @Override
    public List<Invoice> findByInvoiceDateBetween(LocalDate from, LocalDate to) {
        return invoiceMapper.findByInvoiceDateBetween(from, to);
    }

    @Override
    public List<Invoice> findUnpaidByCustomerCode(String customerCode) {
        return invoiceMapper.findUnpaidByCustomerCode(customerCode);
    }

    @Override
    public Optional<Invoice> findLatestByCustomerCode(String customerCode) {
        return invoiceMapper.findLatestByCustomerCode(customerCode);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceMapper.findAll();
    }

    @Override
    @Transactional
    public void update(Invoice invoice) {
        int updatedCount = invoiceMapper.updateWithOptimisticLock(invoice);

        if (updatedCount == 0) {
            Integer currentVersion = invoiceMapper.findVersionById(invoice.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("請求", invoice.getId());
            } else {
                throw new OptimisticLockException("請求", invoice.getId(),
                        invoice.getVersion(), currentVersion);
            }
        }

        invoiceMapper.deleteDetailsByInvoiceId(invoice.getId());
        if (invoice.getDetails() != null) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                detail.setInvoiceId(invoice.getId());
                invoiceMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        invoiceMapper.deleteDetailsByInvoiceId(id);
        invoiceMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        invoiceMapper.deleteAllDetails();
        invoiceMapper.deleteAll();
    }
}
