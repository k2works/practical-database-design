package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.PurchaseOrderDetailRepository;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.infrastructure.out.persistence.mapper.PurchaseOrderDetailMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 発注明細データリポジトリ実装
 */
@Repository
public class PurchaseOrderDetailRepositoryImpl implements PurchaseOrderDetailRepository {

    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    public PurchaseOrderDetailRepositoryImpl(PurchaseOrderDetailMapper purchaseOrderDetailMapper) {
        this.purchaseOrderDetailMapper = purchaseOrderDetailMapper;
    }

    @Override
    public void save(PurchaseOrderDetail detail) {
        purchaseOrderDetailMapper.insert(detail);
    }

    @Override
    public Optional<PurchaseOrderDetail> findById(Integer id) {
        return Optional.ofNullable(purchaseOrderDetailMapper.findById(id));
    }

    @Override
    public List<PurchaseOrderDetail> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return purchaseOrderDetailMapper.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    @Override
    public Optional<PurchaseOrderDetail> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber) {
        return Optional.ofNullable(
                purchaseOrderDetailMapper.findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, lineNumber));
    }

    @Override
    public List<PurchaseOrderDetail> findAll() {
        return purchaseOrderDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        purchaseOrderDetailMapper.deleteAll();
    }
}
