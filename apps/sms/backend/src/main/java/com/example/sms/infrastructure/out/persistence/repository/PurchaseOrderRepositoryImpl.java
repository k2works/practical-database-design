package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.infrastructure.out.persistence.mapper.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 発注リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public void save(PurchaseOrder purchaseOrder) {
        purchaseOrderMapper.insertHeader(purchaseOrder);
        if (purchaseOrder.getDetails() != null) {
            for (PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
                detail.setPurchaseOrderId(purchaseOrder.getId());
                purchaseOrderMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<PurchaseOrder> findById(Integer id) {
        return purchaseOrderMapper.findById(id);
    }

    @Override
    public Optional<PurchaseOrder> findByIdWithDetails(Integer id) {
        return Optional.ofNullable(purchaseOrderMapper.findByIdWithDetails(id));
    }

    @Override
    public Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return purchaseOrderMapper.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    @Override
    public Optional<PurchaseOrder> findWithDetailsByPurchaseOrderNumber(String purchaseOrderNumber) {
        return Optional.ofNullable(purchaseOrderMapper.findWithDetailsByPurchaseOrderNumber(purchaseOrderNumber));
    }

    @Override
    public List<PurchaseOrder> findBySupplierCode(String supplierCode) {
        return purchaseOrderMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
        return purchaseOrderMapper.findByStatus(status);
    }

    @Override
    public List<PurchaseOrder> findByOrderDateBetween(LocalDate from, LocalDate to) {
        return purchaseOrderMapper.findByOrderDateBetween(from, to);
    }

    @Override
    public List<PurchaseOrder> findByDesiredDeliveryDateBetween(LocalDate from, LocalDate to) {
        return purchaseOrderMapper.findByDesiredDeliveryDateBetween(from, to);
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderMapper.findAll();
    }

    @Override
    public PageResult<PurchaseOrder> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<PurchaseOrder> purchaseOrders = purchaseOrderMapper.findWithPagination(offset, size, keyword);
        long totalElements = purchaseOrderMapper.count(keyword);
        return new PageResult<>(purchaseOrders, page, size, totalElements);
    }

    @Override
    @Transactional
    public void update(PurchaseOrder purchaseOrder) {
        int updatedCount = purchaseOrderMapper.updateWithOptimisticLock(purchaseOrder);

        if (updatedCount == 0) {
            // バージョン不一致または削除済み
            Integer currentVersion = purchaseOrderMapper.findVersionById(purchaseOrder.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("発注", purchaseOrder.getId());
            } else {
                throw new OptimisticLockException("発注", purchaseOrder.getId(),
                        purchaseOrder.getVersion(), currentVersion);
            }
        }

        purchaseOrderMapper.deleteDetailsByPurchaseOrderId(purchaseOrder.getId());
        if (purchaseOrder.getDetails() != null) {
            for (PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
                detail.setPurchaseOrderId(purchaseOrder.getId());
                purchaseOrderMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        purchaseOrderMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        purchaseOrderMapper.deleteAll();
    }
}
