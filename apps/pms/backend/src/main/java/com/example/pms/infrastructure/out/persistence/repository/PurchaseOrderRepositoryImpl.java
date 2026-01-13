package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderRepository;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.infrastructure.out.persistence.mapper.PurchaseOrderDetailMapper;
import com.example.pms.infrastructure.out.persistence.mapper.PurchaseOrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 発注データリポジトリ実装
 */
@Repository
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    public PurchaseOrderRepositoryImpl(
            PurchaseOrderMapper purchaseOrderMapper,
            PurchaseOrderDetailMapper purchaseOrderDetailMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderDetailMapper = purchaseOrderDetailMapper;
    }

    @Override
    public void save(PurchaseOrder purchaseOrder) {
        purchaseOrderMapper.insert(purchaseOrder);
        if (purchaseOrder.getDetails() != null) {
            for (var detail : purchaseOrder.getDetails()) {
                purchaseOrderDetailMapper.insert(detail);
            }
        }
    }

    @Override
    public Optional<PurchaseOrder> findById(Integer id) {
        return Optional.ofNullable(purchaseOrderMapper.findById(id));
    }

    @Override
    public Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return Optional.ofNullable(purchaseOrderMapper.findByPurchaseOrderNumber(purchaseOrderNumber));
    }

    @Override
    public Optional<PurchaseOrder> findByPurchaseOrderNumberWithDetails(String purchaseOrderNumber) {
        return Optional.ofNullable(purchaseOrderMapper.findByPurchaseOrderNumberWithDetails(purchaseOrderNumber));
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
    public List<PurchaseOrder> findAll() {
        return purchaseOrderMapper.findAll();
    }

    @Override
    public void updateStatus(Integer id, PurchaseOrderStatus status) {
        purchaseOrderMapper.updateStatus(id, status);
    }

    @Override
    public void deleteAll() {
        purchaseOrderMapper.deleteAll();
    }

    @Override
    public List<PurchaseOrder> findWithPagination(PurchaseOrderStatus status, int limit, int offset) {
        return purchaseOrderMapper.findWithPagination(status, limit, offset);
    }

    @Override
    public long count(PurchaseOrderStatus status) {
        return purchaseOrderMapper.count(status);
    }
}
