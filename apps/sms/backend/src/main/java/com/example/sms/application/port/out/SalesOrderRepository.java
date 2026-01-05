package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 受注リポジトリ（Output Port）.
 */
public interface SalesOrderRepository {

    void save(SalesOrder salesOrder);

    Optional<SalesOrder> findById(Integer id);

    Optional<SalesOrder> findByIdWithDetails(Integer id);

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    Optional<SalesOrder> findWithDetailsByOrderNumber(String orderNumber);

    List<SalesOrder> findByCustomerCode(String customerCode);

    List<SalesOrder> findByStatus(OrderStatus status);

    List<SalesOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

    List<SalesOrder> findByRequestedDeliveryDateBetween(LocalDate from, LocalDate to);

    List<SalesOrder> findAll();

    PageResult<SalesOrder> findWithPagination(int page, int size, String keyword);

    void update(SalesOrder salesOrder);

    void deleteById(Integer id);

    void deleteAll();
}
