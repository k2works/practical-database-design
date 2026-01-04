package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 発注マッパー.
 */
@Mapper
public interface PurchaseOrderMapper {

    void insertHeader(PurchaseOrder purchaseOrder);

    void insertDetail(PurchaseOrderDetail detail);

    Optional<PurchaseOrder> findById(Integer id);

    Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplierCode(String supplierCode);

    List<PurchaseOrder> findByStatus(@Param("status") PurchaseOrderStatus status);

    List<PurchaseOrder> findByOrderDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<PurchaseOrder> findByDesiredDeliveryDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<PurchaseOrder> findAll();

    /**
     * ページネーション付きで発注を検索.
     */
    List<PurchaseOrder> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する発注の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    List<PurchaseOrderDetail> findDetailsByPurchaseOrderId(Integer purchaseOrderId);

    PurchaseOrder findWithDetailsByPurchaseOrderNumber(String purchaseOrderNumber);

    PurchaseOrder findByIdWithDetails(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(PurchaseOrder purchaseOrder);

    int updateWithOptimisticLock(PurchaseOrder purchaseOrder);

    void updateDetail(PurchaseOrderDetail detail);

    void deleteDetailsByPurchaseOrderId(Integer purchaseOrderId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
