package com.example.sms.application.service;

import com.example.sms.application.port.in.QuotationUseCase;
import com.example.sms.application.port.in.command.CreateQuotationCommand;
import com.example.sms.application.port.in.command.UpdateQuotationCommand;
import com.example.sms.application.port.out.QuotationRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.QuotationNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
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
 * 見積アプリケーションサービス.
 */
@Service
@Transactional
public class QuotationService implements QuotationUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final DateTimeFormatter QUOTATION_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final QuotationRepository quotationRepository;

    public QuotationService(QuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    @Override
    public Quotation createQuotation(CreateQuotationCommand command) {
        String quotationNumber = generateQuotationNumber();

        List<QuotationDetail> details = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (command.details() != null) {
            for (CreateQuotationCommand.CreateQuotationDetailCommand detailCmd : command.details()) {
                BigDecimal amount = detailCmd.unitPrice().multiply(detailCmd.quantity());
                BigDecimal detailTax = amount.multiply(TAX_RATE).setScale(0, RoundingMode.DOWN);

                QuotationDetail detail = QuotationDetail.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .productCode(detailCmd.productCode())
                    .productName(detailCmd.productName())
                    .quantity(detailCmd.quantity())
                    .unit(detailCmd.unit())
                    .unitPrice(detailCmd.unitPrice())
                    .amount(amount)
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .taxRate(TAX_RATE_PERCENT)
                    .taxAmount(detailTax)
                    .remarks(detailCmd.remarks())
                    .build();

                details.add(detail);
                subtotal = subtotal.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }
        }

        Quotation quotation = Quotation.builder()
            .quotationNumber(quotationNumber)
            .quotationDate(command.quotationDate() != null ? command.quotationDate() : LocalDate.now())
            .validUntil(command.validUntil())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .salesRepCode(command.salesRepCode())
            .subject(command.subject())
            .subtotal(subtotal)
            .taxAmount(taxAmount)
            .totalAmount(subtotal.add(taxAmount))
            .status(QuotationStatus.NEGOTIATING)
            .remarks(command.remarks())
            .details(details)
            .build();

        quotationRepository.save(quotation);
        return quotation;
    }

    @Override
    public Quotation updateQuotation(String quotationNumber, UpdateQuotationCommand command) {
        Quotation existing = quotationRepository.findWithDetailsByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));

        validateOptimisticLock(command.version(), existing.getVersion(), quotationNumber);

        Quotation updated = Quotation.builder()
            .id(existing.getId())
            .quotationNumber(quotationNumber)
            .quotationDate(existing.getQuotationDate())
            .validUntil(coalesce(command.validUntil(), existing.getValidUntil()))
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .salesRepCode(existing.getSalesRepCode())
            .subject(coalesce(command.subject(), existing.getSubject()))
            .subtotal(existing.getSubtotal())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(coalesce(command.status(), existing.getStatus()))
            .remarks(coalesce(command.remarks(), existing.getRemarks()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        quotationRepository.update(updated);
        return updated;
    }

    private void validateOptimisticLock(Integer commandVersion, Integer existingVersion, String quotationNumber) {
        if (commandVersion != null && !commandVersion.equals(existingVersion)) {
            throw new OptimisticLockException("見積", quotationNumber);
        }
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Quotation> getQuotations(int page, int size, String keyword) {
        return quotationRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Quotation getQuotationByNumber(String quotationNumber) {
        return quotationRepository.findByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Quotation getQuotationWithDetails(String quotationNumber) {
        return quotationRepository.findWithDetailsByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> getQuotationsByStatus(QuotationStatus status) {
        return quotationRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quotation> getQuotationsByCustomer(String customerCode) {
        return quotationRepository.findByCustomerCode(customerCode);
    }

    @Override
    public void deleteQuotation(String quotationNumber) {
        Quotation existing = quotationRepository.findByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));

        quotationRepository.deleteById(existing.getId());
    }

    @Override
    public Quotation confirmQuotation(String quotationNumber, Integer version) {
        Quotation existing = quotationRepository.findWithDetailsByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));

        validateOptimisticLock(version, existing.getVersion(), quotationNumber);

        Quotation confirmed = Quotation.builder()
            .id(existing.getId())
            .quotationNumber(quotationNumber)
            .quotationDate(existing.getQuotationDate())
            .validUntil(existing.getValidUntil())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .salesRepCode(existing.getSalesRepCode())
            .subject(existing.getSubject())
            .subtotal(existing.getSubtotal())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(QuotationStatus.ORDERED)
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        quotationRepository.update(confirmed);
        return confirmed;
    }

    @Override
    public Quotation loseQuotation(String quotationNumber, Integer version) {
        Quotation existing = quotationRepository.findWithDetailsByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));

        validateOptimisticLock(version, existing.getVersion(), quotationNumber);

        Quotation lost = Quotation.builder()
            .id(existing.getId())
            .quotationNumber(quotationNumber)
            .quotationDate(existing.getQuotationDate())
            .validUntil(existing.getValidUntil())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .salesRepCode(existing.getSalesRepCode())
            .subject(existing.getSubject())
            .subtotal(existing.getSubtotal())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(QuotationStatus.LOST)
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        quotationRepository.update(lost);
        return lost;
    }

    private String generateQuotationNumber() {
        String datePrefix = LocalDate.now().format(QUOTATION_NUMBER_FORMAT);
        List<Quotation> todayQuotations = quotationRepository.findByQuotationDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayQuotations.size() + 1;
        return String.format("QT-%s-%04d", datePrefix, sequence);
    }
}
