package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売上リポジトリ（Output Port）.
 */
public interface SalesRepository {

    void save(Sales sales);

    Optional<Sales> findById(Integer id);

    Optional<Sales> findBySalesNumber(String salesNumber);

    List<Sales> findByOrderId(Integer orderId);

    List<Sales> findByShipmentId(Integer shipmentId);

    List<Sales> findByCustomerCode(String customerCode);

    List<Sales> findByStatus(SalesStatus status);

    List<Sales> findBySalesDateBetween(LocalDate from, LocalDate to);

    List<Sales> findAll();

    PageResult<Sales> findWithPagination(int page, int size, String keyword);

    void update(Sales sales);

    void deleteById(Integer id);

    void deleteAll();
}
