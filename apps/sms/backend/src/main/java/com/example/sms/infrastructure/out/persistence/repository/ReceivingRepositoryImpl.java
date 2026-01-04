package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import com.example.sms.infrastructure.out.persistence.mapper.ReceivingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入荷リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ReceivingRepositoryImpl implements ReceivingRepository {

    private final ReceivingMapper receivingMapper;

    @Override
    public void save(Receiving receiving) {
        receivingMapper.insertHeader(receiving);
        if (receiving.getDetails() != null) {
            for (ReceivingDetail detail : receiving.getDetails()) {
                detail.setReceivingId(receiving.getId());
                receivingMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Receiving> findById(Integer id) {
        return receivingMapper.findById(id);
    }

    @Override
    public Optional<Receiving> findByIdWithDetails(Integer id) {
        return Optional.ofNullable(receivingMapper.findByIdWithDetails(id));
    }

    @Override
    public Optional<Receiving> findByReceivingNumber(String receivingNumber) {
        return receivingMapper.findByReceivingNumber(receivingNumber);
    }

    @Override
    public Optional<Receiving> findWithDetailsByReceivingNumber(String receivingNumber) {
        return Optional.ofNullable(receivingMapper.findWithDetailsByReceivingNumber(receivingNumber));
    }

    @Override
    public List<Receiving> findByPurchaseOrderId(Integer purchaseOrderId) {
        return receivingMapper.findByPurchaseOrderId(purchaseOrderId);
    }

    @Override
    public List<Receiving> findBySupplierCode(String supplierCode) {
        return receivingMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<Receiving> findByStatus(ReceivingStatus status) {
        return receivingMapper.findByStatus(status);
    }

    @Override
    public List<Receiving> findByReceivingDateBetween(LocalDate from, LocalDate to) {
        return receivingMapper.findByReceivingDateBetween(from, to);
    }

    @Override
    public List<Receiving> findAll() {
        return receivingMapper.findAll();
    }

    @Override
    public PageResult<Receiving> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Receiving> receivings = receivingMapper.findWithPagination(offset, size, keyword);
        long totalElements = receivingMapper.count(keyword);
        return new PageResult<>(receivings, page, size, totalElements);
    }

    @Override
    @Transactional
    public void update(Receiving receiving) {
        int updatedCount = receivingMapper.updateWithOptimisticLock(receiving);

        if (updatedCount == 0) {
            // バージョン不一致または削除済み
            Integer currentVersion = receivingMapper.findVersionById(receiving.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("入荷", receiving.getId());
            } else {
                throw new OptimisticLockException("入荷", receiving.getId(),
                        receiving.getVersion(), currentVersion);
            }
        }

        receivingMapper.deleteDetailsByReceivingId(receiving.getId());
        if (receiving.getDetails() != null) {
            for (ReceivingDetail detail : receiving.getDetails()) {
                detail.setReceivingId(receiving.getId());
                receivingMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        receivingMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        receivingMapper.deleteAll();
    }
}
