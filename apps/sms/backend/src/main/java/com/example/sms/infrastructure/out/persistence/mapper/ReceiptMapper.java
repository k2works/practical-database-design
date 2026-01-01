package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptApplication;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入金マッパー.
 */
@Mapper
public interface ReceiptMapper {

    void insertHeader(Receipt receipt);

    void insertApplication(ReceiptApplication application);

    Optional<Receipt> findById(Integer id);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    List<Receipt> findByCustomerCode(String customerCode);

    List<Receipt> findByStatus(@Param("status") ReceiptStatus status);

    List<Receipt> findByReceiptDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    BigDecimal sumReceiptsByCustomerAndDateRange(
            @Param("customerCode") String customerCode,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<Receipt> findAll();

    List<ReceiptApplication> findApplicationsByReceiptId(Integer receiptId);

    Receipt findWithApplicationsByReceiptNumber(String receiptNumber);

    Receipt findByIdWithApplications(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(Receipt receipt);

    int updateWithOptimisticLock(Receipt receipt);

    void updateAmounts(@Param("receiptId") Integer receiptId,
                       @Param("appliedAmount") BigDecimal appliedAmount,
                       @Param("unappliedAmount") BigDecimal unappliedAmount);

    void updateStatus(@Param("receiptId") Integer receiptId, @Param("status") ReceiptStatus status);

    void deleteApplicationsByReceiptId(Integer receiptId);

    void deleteById(Integer id);

    void deleteAllApplications();

    void deleteAll();
}
