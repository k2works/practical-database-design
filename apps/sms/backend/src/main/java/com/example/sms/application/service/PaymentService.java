package com.example.sms.application.service;

import com.example.sms.application.port.in.PaymentUseCase;
import com.example.sms.application.port.in.command.CreatePaymentCommand;
import com.example.sms.application.port.out.PaymentRepository;
import com.example.sms.domain.exception.PaymentNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentDetail;
import com.example.sms.domain.model.payment.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払アプリケーションサービス.
 */
@Service
@Transactional
public class PaymentService implements PaymentUseCase {

    private static final DateTimeFormatter PAYMENT_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createPayment(CreatePaymentCommand command) {
        String paymentNumber = generatePaymentNumber();

        List<PaymentDetail> details = new ArrayList<>();
        int lineNumber = 1;
        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        for (CreatePaymentCommand.CreatePaymentDetailCommand detailCmd : command.details()) {
            PaymentDetail detail = PaymentDetail.builder()
                .lineNumber(lineNumber++)
                .purchaseNumber(detailCmd.purchaseNumber())
                .purchaseDate(detailCmd.purchaseDate())
                .purchaseAmount(detailCmd.purchaseAmount())
                .taxAmount(detailCmd.taxAmount())
                .paymentTargetAmount(detailCmd.paymentTargetAmount())
                .build();
            details.add(detail);

            if (detailCmd.purchaseAmount() != null) {
                totalPaymentAmount = totalPaymentAmount.add(detailCmd.purchaseAmount());
            }
            if (detailCmd.taxAmount() != null) {
                totalTaxAmount = totalTaxAmount.add(detailCmd.taxAmount());
            }
        }

        Payment payment = Payment.builder()
            .paymentNumber(paymentNumber)
            .supplierCode(command.supplierCode())
            .paymentClosingDate(command.paymentClosingDate())
            .paymentDueDate(command.paymentDueDate())
            .paymentMethod(command.paymentMethod())
            .paymentAmount(totalPaymentAmount)
            .taxAmount(totalTaxAmount)
            .withholdingAmount(BigDecimal.ZERO)
            .netPaymentAmount(totalPaymentAmount.add(totalTaxAmount))
            .bankCode(command.bankCode())
            .branchCode(command.branchCode())
            .accountType(command.accountType())
            .accountNumber(command.accountNumber())
            .accountName(command.accountName())
            .remarks(command.remarks())
            .status(PaymentStatus.DRAFT)
            .details(details)
            .build();

        paymentRepository.save(payment);
        return payment;
    }

    private String generatePaymentNumber() {
        String datePrefix = LocalDate.now().format(PAYMENT_NUMBER_FORMAT);
        List<Payment> todayPayments = paymentRepository.findByPaymentDueDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayPayments.size() + 1;
        return String.format("PAY-%s-%04d", datePrefix, sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Payment> getPayments(int page, int size, String keyword) {
        return paymentRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber)
            .orElseThrow(() -> new PaymentNotFoundException(paymentNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentWithDetails(String paymentNumber) {
        return paymentRepository.findWithDetailsByPaymentNumber(paymentNumber)
            .orElseThrow(() -> new PaymentNotFoundException(paymentNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsBySupplier(String supplierCode) {
        return paymentRepository.findBySupplierCode(supplierCode);
    }
}
