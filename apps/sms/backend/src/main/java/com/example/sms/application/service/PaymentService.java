package com.example.sms.application.service;

import com.example.sms.application.port.in.PaymentUseCase;
import com.example.sms.application.port.out.PaymentRepository;
import com.example.sms.domain.exception.PaymentNotFoundException;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 支払アプリケーションサービス.
 */
@Service
@Transactional
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
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
