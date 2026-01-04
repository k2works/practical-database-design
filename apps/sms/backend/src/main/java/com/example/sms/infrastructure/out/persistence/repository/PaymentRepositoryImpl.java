package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PaymentRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentDetail;
import com.example.sms.domain.model.payment.PaymentStatus;
import com.example.sms.infrastructure.out.persistence.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 支払リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public void save(Payment payment) {
        paymentMapper.insertHeader(payment);

        if (payment.getDetails() != null && !payment.getDetails().isEmpty()) {
            int lineNumber = 1;
            for (PaymentDetail detail : payment.getDetails()) {
                detail.setPaymentId(payment.getId());
                detail.setLineNumber(lineNumber++);
                paymentMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Payment> findById(Integer id) {
        return paymentMapper.findById(id);
    }

    @Override
    public Optional<Payment> findByPaymentNumber(String paymentNumber) {
        return paymentMapper.findByPaymentNumber(paymentNumber);
    }

    @Override
    public Optional<Payment> findWithDetailsByPaymentNumber(String paymentNumber) {
        Payment payment = paymentMapper.findWithDetailsByPaymentNumber(paymentNumber);
        return Optional.ofNullable(payment);
    }

    @Override
    public List<Payment> findBySupplierCode(String supplierCode) {
        return paymentMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentMapper.findByStatus(status);
    }

    @Override
    public List<Payment> findByPaymentDueDateBetween(LocalDate from, LocalDate to) {
        return paymentMapper.findByPaymentDueDateBetween(from, to);
    }

    @Override
    public List<Payment> findAll() {
        return paymentMapper.findAll();
    }

    @Override
    public PageResult<Payment> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Payment> payments = paymentMapper.findWithPagination(offset, size, keyword);
        long totalElements = paymentMapper.count(keyword);
        return new PageResult<>(payments, page, size, totalElements);
    }

    @Override
    @Transactional
    public void update(Payment payment) {
        int updatedCount = paymentMapper.updateWithOptimisticLock(payment);

        if (updatedCount == 0) {
            Integer currentVersion = paymentMapper.findVersionById(payment.getId());

            if (currentVersion == null) {
                throw new OptimisticLockException("支払", payment.getId());
            } else {
                throw new OptimisticLockException(
                        "支払",
                        payment.getId(),
                        payment.getVersion(),
                        currentVersion
                );
            }
        }

        if (payment.getDetails() != null) {
            paymentMapper.deleteDetailsByPaymentId(payment.getId());
            int lineNumber = 1;
            for (PaymentDetail detail : payment.getDetails()) {
                detail.setPaymentId(payment.getId());
                detail.setLineNumber(lineNumber++);
                paymentMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        paymentMapper.deleteDetailsByPaymentId(id);
        paymentMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        paymentMapper.deleteAllDetails();
        paymentMapper.deleteAll();
    }
}
