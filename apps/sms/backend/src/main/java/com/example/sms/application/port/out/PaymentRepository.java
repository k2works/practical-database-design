package com.example.sms.application.port.out;

import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 支払リポジトリ.
 */
public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findById(Integer id);

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findWithDetailsByPaymentNumber(String paymentNumber);

    List<Payment> findBySupplierCode(String supplierCode);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentDueDateBetween(LocalDate from, LocalDate to);

    List<Payment> findAll();

    void update(Payment payment);

    void deleteById(Integer id);

    void deleteAll();
}
