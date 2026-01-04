package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.SalesRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesDetail;
import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.infrastructure.out.persistence.mapper.SalesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売上リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class SalesRepositoryImpl implements SalesRepository {

    private final SalesMapper salesMapper;

    @Override
    public void save(Sales sales) {
        salesMapper.insertHeader(sales);
        if (sales.getDetails() != null) {
            for (SalesDetail detail : sales.getDetails()) {
                detail.setSalesId(sales.getId());
                salesMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Sales> findById(Integer id) {
        return salesMapper.findById(id);
    }

    @Override
    public Optional<Sales> findBySalesNumber(String salesNumber) {
        return salesMapper.findBySalesNumber(salesNumber);
    }

    @Override
    public List<Sales> findByOrderId(Integer orderId) {
        return salesMapper.findByOrderId(orderId);
    }

    @Override
    public List<Sales> findByShipmentId(Integer shipmentId) {
        return salesMapper.findByShipmentId(shipmentId);
    }

    @Override
    public List<Sales> findByCustomerCode(String customerCode) {
        return salesMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<Sales> findByStatus(SalesStatus status) {
        return salesMapper.findByStatus(status);
    }

    @Override
    public List<Sales> findBySalesDateBetween(LocalDate from, LocalDate to) {
        return salesMapper.findBySalesDateBetween(from, to);
    }

    @Override
    public List<Sales> findAll() {
        return salesMapper.findAll();
    }

    @Override
    public PageResult<Sales> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<Sales> salesList = salesMapper.findWithPagination(offset, size, keyword);
        long totalElements = salesMapper.count(keyword);
        return new PageResult<>(salesList, page, size, totalElements);
    }

    @Override
    public void update(Sales sales) {
        salesMapper.updateHeader(sales);
        salesMapper.deleteDetailsBySalesId(sales.getId());
        if (sales.getDetails() != null) {
            for (SalesDetail detail : sales.getDetails()) {
                detail.setSalesId(sales.getId());
                salesMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        salesMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        salesMapper.deleteAll();
    }
}
