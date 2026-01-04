package com.example.sms.application.service;

import com.example.sms.application.port.in.ReceiptUseCase;
import com.example.sms.application.port.in.command.CreateReceiptCommand;
import com.example.sms.application.port.in.command.UpdateReceiptCommand;
import com.example.sms.application.port.out.ReceiptRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.ReceiptNotFoundException;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 入金アプリケーションサービス.
 */
@Service
@Transactional
public class ReceiptService implements ReceiptUseCase {

    private static final DateTimeFormatter RECEIPT_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public Receipt createReceipt(CreateReceiptCommand command) {
        String receiptNumber = generateReceiptNumber();

        BigDecimal bankFee = command.bankFee() != null ? command.bankFee() : BigDecimal.ZERO;
        BigDecimal unappliedAmount = command.receiptAmount().subtract(bankFee);

        Receipt receipt = Receipt.builder()
            .receiptNumber(receiptNumber)
            .receiptDate(command.receiptDate() != null ? command.receiptDate() : LocalDate.now())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .receiptMethod(command.receiptMethod())
            .receiptAmount(command.receiptAmount())
            .appliedAmount(BigDecimal.ZERO)
            .unappliedAmount(unappliedAmount)
            .bankFee(bankFee)
            .payerName(command.payerName())
            .bankName(command.bankName())
            .accountNumber(command.accountNumber())
            .status(ReceiptStatus.RECEIVED)
            .remarks(command.remarks())
            .build();

        receiptRepository.save(receipt);
        return receipt;
    }

    @Override
    public Receipt updateReceipt(String receiptNumber, UpdateReceiptCommand command) {
        Receipt existing = receiptRepository.findByReceiptNumber(receiptNumber)
            .orElseThrow(() -> new ReceiptNotFoundException(receiptNumber));

        if (command.version() != null && !command.version().equals(existing.getVersion())) {
            throw new OptimisticLockException("入金", receiptNumber);
        }

        Receipt updated = Receipt.builder()
            .id(existing.getId())
            .receiptNumber(receiptNumber)
            .receiptDate(existing.getReceiptDate())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .receiptMethod(existing.getReceiptMethod())
            .receiptAmount(existing.getReceiptAmount())
            .appliedAmount(existing.getAppliedAmount())
            .unappliedAmount(existing.getUnappliedAmount())
            .bankFee(existing.getBankFee())
            .payerName(existing.getPayerName())
            .bankName(existing.getBankName())
            .accountNumber(existing.getAccountNumber())
            .status(command.status() != null ? command.status() : existing.getStatus())
            .remarks(command.remarks() != null ? command.remarks() : existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .applications(existing.getApplications())
            .build();

        receiptRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Receipt getReceiptByNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber)
            .orElseThrow(() -> new ReceiptNotFoundException(receiptNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> getReceiptsByStatus(ReceiptStatus status) {
        return receiptRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> getReceiptsByCustomer(String customerCode) {
        return receiptRepository.findByCustomerCode(customerCode);
    }

    @Override
    public void deleteReceipt(String receiptNumber) {
        Receipt existing = receiptRepository.findByReceiptNumber(receiptNumber)
            .orElseThrow(() -> new ReceiptNotFoundException(receiptNumber));

        receiptRepository.deleteById(existing.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receipt> getReceiptsByDateRange(LocalDate from, LocalDate to) {
        return receiptRepository.findByReceiptDateBetween(from, to);
    }

    private String generateReceiptNumber() {
        String datePrefix = LocalDate.now().format(RECEIPT_NUMBER_FORMAT);
        List<Receipt> todayReceipts = receiptRepository.findByReceiptDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayReceipts.size() + 1;
        return String.format("RCP-%s-%04d", datePrefix, sequence);
    }
}
