package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import com.example.sms.infrastructure.out.persistence.mapper.SalesOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 受注リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class SalesOrderRepositoryImpl implements SalesOrderRepository {

    private final SalesOrderMapper salesOrderMapper;

    @Override
    public void save(SalesOrder salesOrder) {
        salesOrderMapper.insertHeader(salesOrder);
        if (salesOrder.getDetails() != null) {
            for (SalesOrderDetail detail : salesOrder.getDetails()) {
                detail.setOrderId(salesOrder.getId());
                salesOrderMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<SalesOrder> findById(Integer id) {
        return salesOrderMapper.findById(id);
    }

    @Override
    public Optional<SalesOrder> findByIdWithDetails(Integer id) {
        return Optional.ofNullable(salesOrderMapper.findByIdWithDetails(id));
    }

    @Override
    public Optional<SalesOrder> findByOrderNumber(String orderNumber) {
        return salesOrderMapper.findByOrderNumber(orderNumber);
    }

    @Override
    public Optional<SalesOrder> findWithDetailsByOrderNumber(String orderNumber) {
        return Optional.ofNullable(salesOrderMapper.findWithDetailsByOrderNumber(orderNumber));
    }

    @Override
    public List<SalesOrder> findByCustomerCode(String customerCode) {
        return salesOrderMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<SalesOrder> findByStatus(OrderStatus status) {
        return salesOrderMapper.findByStatus(status);
    }

    @Override
    public List<SalesOrder> findByOrderDateBetween(LocalDate from, LocalDate to) {
        return salesOrderMapper.findByOrderDateBetween(from, to);
    }

    @Override
    public List<SalesOrder> findByRequestedDeliveryDateBetween(LocalDate from, LocalDate to) {
        return salesOrderMapper.findByRequestedDeliveryDateBetween(from, to);
    }

    @Override
    public List<SalesOrder> findAll() {
        return salesOrderMapper.findAll();
    }

    @Override
    @Transactional
    public void update(SalesOrder salesOrder) {
        int updatedCount = salesOrderMapper.updateWithOptimisticLock(salesOrder);

        if (updatedCount == 0) {
            // バージョン不一致または削除済み
            Integer currentVersion = salesOrderMapper.findVersionById(salesOrder.getId());
            if (currentVersion == null) {
                throw new OptimisticLockException("受注", salesOrder.getId());
            } else {
                throw new OptimisticLockException("受注", salesOrder.getId(),
                        salesOrder.getVersion(), currentVersion);
            }
        }

        salesOrderMapper.deleteDetailsByOrderId(salesOrder.getId());
        if (salesOrder.getDetails() != null) {
            for (SalesOrderDetail detail : salesOrder.getDetails()) {
                detail.setOrderId(salesOrder.getId());
                salesOrderMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        salesOrderMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        salesOrderMapper.deleteAll();
    }
}
