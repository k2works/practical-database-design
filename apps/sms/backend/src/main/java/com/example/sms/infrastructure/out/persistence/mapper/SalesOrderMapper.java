package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 受注マッパー.
 */
@Mapper
public interface SalesOrderMapper {

    void insertHeader(SalesOrder salesOrder);

    void insertDetail(SalesOrderDetail detail);

    Optional<SalesOrder> findById(Integer id);

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    List<SalesOrder> findByCustomerCode(String customerCode);

    List<SalesOrder> findByStatus(@Param("status") OrderStatus status);

    List<SalesOrder> findByOrderDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<SalesOrder> findByRequestedDeliveryDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<SalesOrder> findAll();

    List<SalesOrderDetail> findDetailsByOrderId(Integer orderId);

    void updateHeader(SalesOrder salesOrder);

    void updateDetail(SalesOrderDetail detail);

    void deleteDetailsByOrderId(Integer orderId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
