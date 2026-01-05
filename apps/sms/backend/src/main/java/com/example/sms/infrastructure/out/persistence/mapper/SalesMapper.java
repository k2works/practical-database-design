package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesDetail;
import com.example.sms.domain.model.sales.SalesStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 売上マッパー.
 */
@Mapper
public interface SalesMapper {

    void insertHeader(Sales sales);

    void insertDetail(SalesDetail detail);

    Optional<Sales> findById(Integer id);

    Optional<Sales> findBySalesNumber(String salesNumber);

    List<Sales> findByOrderId(Integer orderId);

    List<Sales> findByShipmentId(Integer shipmentId);

    List<Sales> findByCustomerCode(String customerCode);

    List<Sales> findByStatus(@Param("status") SalesStatus status);

    List<Sales> findBySalesDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Sales> findAll();

    /**
     * ページネーション付きで売上を検索.
     */
    List<Sales> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する売上の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    List<SalesDetail> findDetailsBySalesId(Integer salesId);

    void updateHeader(Sales sales);

    void deleteDetailsBySalesId(Integer salesId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
