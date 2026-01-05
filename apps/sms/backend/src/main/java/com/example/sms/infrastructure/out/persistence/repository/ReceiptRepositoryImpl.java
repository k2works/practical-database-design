package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ReceiptRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptApplication;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.infrastructure.out.persistence.mapper.ReceiptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入金リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ReceiptRepositoryImpl implements ReceiptRepository {

    private final ReceiptMapper receiptMapper;

    @Override
    public void save(Receipt receipt) {
        receiptMapper.insertHeader(receipt);
        if (receipt.getApplications() != null) {
            for (ReceiptApplication application : receipt.getApplications()) {
                application.setReceiptId(receipt.getId());
                receiptMapper.insertApplication(application);
            }
        }
    }

    @Override
    public PageResult<Receipt> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Receipt> receipts = receiptMapper.findWithPagination(offset, size, keyword);
        long totalElements = receiptMapper.count(keyword);
        return new PageResult<>(receipts, page, size, totalElements);
    }

    @Override
    public Optional<Receipt> findById(Integer id) {
        return receiptMapper.findById(id);
    }

    @Override
    public Optional<Receipt> findByIdWithApplications(Integer id) {
        return Optional.ofNullable(receiptMapper.findByIdWithApplications(id));
    }

    @Override
    public Optional<Receipt> findByReceiptNumber(String receiptNumber) {
        return receiptMapper.findByReceiptNumber(receiptNumber);
    }

    @Override
    public Optional<Receipt> findWithApplicationsByReceiptNumber(String receiptNumber) {
        return Optional.ofNullable(receiptMapper.findWithApplicationsByReceiptNumber(receiptNumber));
    }

    @Override
    public List<Receipt> findByCustomerCode(String customerCode) {
        return receiptMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<Receipt> findByStatus(ReceiptStatus status) {
        return receiptMapper.findByStatus(status);
    }

    @Override
    public List<Receipt> findByReceiptDateBetween(LocalDate from, LocalDate to) {
        return receiptMapper.findByReceiptDateBetween(from, to);
    }

    @Override
    public BigDecimal sumReceiptsByCustomerAndDateRange(String customerCode, LocalDate from, LocalDate to) {
        return receiptMapper.sumReceiptsByCustomerAndDateRange(customerCode, from, to);
    }

    @Override
    public List<Receipt> findAll() {
        return receiptMapper.findAll();
    }

    @Override
    @Transactional
    public void update(Receipt receipt) {
        int updatedCount = receiptMapper.updateWithOptimisticLock(receipt);

        if (updatedCount == 0) {
            Integer currentVersion = receiptMapper.findVersionById(receipt.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("入金", receipt.getId());
            } else {
                throw new OptimisticLockException("入金", receipt.getId(),
                        receipt.getVersion(), currentVersion);
            }
        }

        receiptMapper.deleteApplicationsByReceiptId(receipt.getId());
        if (receipt.getApplications() != null) {
            for (ReceiptApplication application : receipt.getApplications()) {
                application.setReceiptId(receipt.getId());
                receiptMapper.insertApplication(application);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        receiptMapper.deleteApplicationsByReceiptId(id);
        receiptMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        receiptMapper.deleteAllApplications();
        receiptMapper.deleteAll();
    }
}
