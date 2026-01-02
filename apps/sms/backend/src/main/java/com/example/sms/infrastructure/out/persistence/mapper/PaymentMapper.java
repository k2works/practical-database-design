package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentDetail;
import com.example.sms.domain.model.payment.PaymentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 支払 Mapper.
 */
@Mapper
public interface PaymentMapper {

    void insertHeader(Payment payment);

    void insertDetail(PaymentDetail detail);

    Optional<Payment> findById(Integer id);

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Payment findWithDetailsByPaymentNumber(String paymentNumber);

    List<Payment> findBySupplierCode(String supplierCode);

    List<Payment> findByStatus(@Param("status") PaymentStatus status);

    List<Payment> findByPaymentDueDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Payment> findAll();

    int updateWithOptimisticLock(Payment payment);

    Integer findVersionById(Integer id);

    void deleteDetailsByPaymentId(Integer paymentId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
