package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PurchaseRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import com.example.sms.infrastructure.out.persistence.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 仕入リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepository {

    private final PurchaseMapper purchaseMapper;

    @Override
    public void save(Purchase purchase) {
        purchaseMapper.insertHeader(purchase);
        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                detail.setPurchaseId(purchase.getId());
                purchaseMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Purchase> findById(Integer id) {
        return purchaseMapper.findById(id);
    }

    @Override
    public Optional<Purchase> findByIdWithDetails(Integer id) {
        return Optional.ofNullable(purchaseMapper.findByIdWithDetails(id));
    }

    @Override
    public Optional<Purchase> findByPurchaseNumber(String purchaseNumber) {
        return purchaseMapper.findByPurchaseNumber(purchaseNumber);
    }

    @Override
    public Optional<Purchase> findWithDetailsByPurchaseNumber(String purchaseNumber) {
        return Optional.ofNullable(purchaseMapper.findWithDetailsByPurchaseNumber(purchaseNumber));
    }

    @Override
    public List<Purchase> findByReceivingId(Integer receivingId) {
        return purchaseMapper.findByReceivingId(receivingId);
    }

    @Override
    public List<Purchase> findBySupplierCode(String supplierCode) {
        return purchaseMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<Purchase> findByPurchaseDateBetween(LocalDate from, LocalDate to) {
        return purchaseMapper.findByPurchaseDateBetween(from, to);
    }

    @Override
    public List<Purchase> findAll() {
        return purchaseMapper.findAll();
    }

    @Override
    @Transactional
    public void update(Purchase purchase) {
        int updatedCount = purchaseMapper.updateWithOptimisticLock(purchase);

        if (updatedCount == 0) {
            // バージョン不一致または削除済み
            Integer currentVersion = purchaseMapper.findVersionById(purchase.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("仕入", purchase.getId());
            } else {
                throw new OptimisticLockException("仕入", purchase.getId(),
                        purchase.getVersion(), currentVersion);
            }
        }

        purchaseMapper.deleteDetailsByPurchaseId(purchase.getId());
        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                detail.setPurchaseId(purchase.getId());
                purchaseMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        purchaseMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        purchaseMapper.deleteAll();
    }
}
