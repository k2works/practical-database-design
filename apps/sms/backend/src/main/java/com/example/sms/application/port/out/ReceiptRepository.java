package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入金リポジトリ（Output Port）.
 */
public interface ReceiptRepository {

    void save(Receipt receipt);

    /**
     * ページネーション付きで入金を検索.
     */
    PageResult<Receipt> findWithPagination(int page, int size, String keyword);

    Optional<Receipt> findById(Integer id);

    Optional<Receipt> findByIdWithApplications(Integer id);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    Optional<Receipt> findWithApplicationsByReceiptNumber(String receiptNumber);

    List<Receipt> findByCustomerCode(String customerCode);

    List<Receipt> findByStatus(ReceiptStatus status);

    List<Receipt> findByReceiptDateBetween(LocalDate from, LocalDate to);

    BigDecimal sumReceiptsByCustomerAndDateRange(String customerCode, LocalDate from, LocalDate to);

    List<Receipt> findAll();

    void update(Receipt receipt);

    void deleteById(Integer id);

    void deleteAll();
}
