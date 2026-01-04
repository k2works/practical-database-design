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

    /**
     * ページネーション付きで受注を検索.
     */
    List<SalesOrder> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する受注の件数を取得.
     */
    long count(@Param("keyword") String keyword);

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

    SalesOrder findWithDetailsByOrderNumber(String orderNumber);

    SalesOrder findByIdWithDetails(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(SalesOrder salesOrder);

    int updateWithOptimisticLock(SalesOrder salesOrder);

    void updateDetail(SalesOrderDetail detail);

    void deleteDetailsByOrderId(Integer orderId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
