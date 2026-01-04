package com.example.sms.application.service;

import com.example.sms.application.port.in.InvoiceUseCase;
import com.example.sms.application.port.in.command.CreateInvoiceCommand;
import com.example.sms.application.port.in.command.UpdateInvoiceCommand;
import com.example.sms.application.port.out.InvoiceRepository;
import com.example.sms.domain.exception.InvoiceNotFoundException;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.invoice.InvoiceType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 請求アプリケーションサービス.
 */
@Service
@Transactional
public class InvoiceService implements InvoiceUseCase {

    private static final DateTimeFormatter INVOICE_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Invoice createInvoice(CreateInvoiceCommand command) {
        String invoiceNumber = generateInvoiceNumber();

        BigDecimal carriedBalance = command.previousBalance()
            .subtract(command.receiptAmount() != null ? command.receiptAmount() : BigDecimal.ZERO);
        BigDecimal currentInvoiceAmount = command.currentSalesAmount()
            .add(command.currentTaxAmount() != null ? command.currentTaxAmount() : BigDecimal.ZERO);
        BigDecimal invoiceBalance = carriedBalance.add(currentInvoiceAmount);

        Invoice invoice = Invoice.builder()
            .invoiceNumber(invoiceNumber)
            .invoiceDate(command.invoiceDate() != null ? command.invoiceDate() : LocalDate.now())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .closingDate(command.closingDate())
            .invoiceType(InvoiceType.CLOSING)
            .previousBalance(command.previousBalance() != null ? command.previousBalance() : BigDecimal.ZERO)
            .receiptAmount(command.receiptAmount() != null ? command.receiptAmount() : BigDecimal.ZERO)
            .carriedBalance(carriedBalance)
            .currentSalesAmount(command.currentSalesAmount() != null ? command.currentSalesAmount() : BigDecimal.ZERO)
            .currentTaxAmount(command.currentTaxAmount() != null ? command.currentTaxAmount() : BigDecimal.ZERO)
            .currentInvoiceAmount(currentInvoiceAmount)
            .invoiceBalance(invoiceBalance)
            .dueDate(command.dueDate())
            .status(InvoiceStatus.DRAFT)
            .remarks(command.remarks())
            .build();

        invoiceRepository.save(invoice);
        return invoice;
    }

    @Override
    public Invoice updateInvoice(String invoiceNumber, UpdateInvoiceCommand command) {
        Invoice existing = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));

        if (command.version() != null && !command.version().equals(existing.getVersion())) {
            throw new OptimisticLockException("請求", invoiceNumber);
        }

        Invoice updated = Invoice.builder()
            .id(existing.getId())
            .invoiceNumber(invoiceNumber)
            .invoiceDate(existing.getInvoiceDate())
            .billingCode(existing.getBillingCode())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .closingDate(existing.getClosingDate())
            .invoiceType(existing.getInvoiceType())
            .previousBalance(existing.getPreviousBalance())
            .receiptAmount(existing.getReceiptAmount())
            .carriedBalance(existing.getCarriedBalance())
            .currentSalesAmount(existing.getCurrentSalesAmount())
            .currentTaxAmount(existing.getCurrentTaxAmount())
            .currentInvoiceAmount(existing.getCurrentInvoiceAmount())
            .invoiceBalance(existing.getInvoiceBalance())
            .dueDate(existing.getDueDate())
            .status(command.status() != null ? command.status() : existing.getStatus())
            .remarks(command.remarks() != null ? command.remarks() : existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        invoiceRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Invoice> getInvoices(int page, int size, String keyword) {
        return invoiceRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomer(String customerCode) {
        return invoiceRepository.findByCustomerCode(customerCode);
    }

    @Override
    public Invoice issueInvoice(String invoiceNumber, Integer version) {
        Invoice existing = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));

        if (version != null && !version.equals(existing.getVersion())) {
            throw new OptimisticLockException("請求", invoiceNumber);
        }

        Invoice issued = Invoice.builder()
            .id(existing.getId())
            .invoiceNumber(invoiceNumber)
            .invoiceDate(existing.getInvoiceDate())
            .billingCode(existing.getBillingCode())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .closingDate(existing.getClosingDate())
            .invoiceType(existing.getInvoiceType())
            .previousBalance(existing.getPreviousBalance())
            .receiptAmount(existing.getReceiptAmount())
            .carriedBalance(existing.getCarriedBalance())
            .currentSalesAmount(existing.getCurrentSalesAmount())
            .currentTaxAmount(existing.getCurrentTaxAmount())
            .currentInvoiceAmount(existing.getCurrentInvoiceAmount())
            .invoiceBalance(existing.getInvoiceBalance())
            .dueDate(existing.getDueDate())
            .status(InvoiceStatus.ISSUED)
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        invoiceRepository.update(issued);
        return issued;
    }

    @Override
    public void deleteInvoice(String invoiceNumber) {
        Invoice existing = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));

        invoiceRepository.deleteById(existing.getId());
    }

    private String generateInvoiceNumber() {
        String datePrefix = LocalDate.now().format(INVOICE_NUMBER_FORMAT);
        List<Invoice> todayInvoices = invoiceRepository.findByInvoiceDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayInvoices.size() + 1;
        return String.format("INV-%s-%04d", datePrefix, sequence);
    }
}
