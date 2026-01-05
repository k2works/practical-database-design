package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 仕入マッパー.
 */
@Mapper
public interface PurchaseMapper {

    /**
     * ページネーション付きで仕入を検索.
     */
    List<Purchase> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する仕入の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    void insertHeader(Purchase purchase);

    void insertDetail(PurchaseDetail detail);

    Optional<Purchase> findById(Integer id);

    Optional<Purchase> findByPurchaseNumber(String purchaseNumber);

    List<Purchase> findByReceivingId(Integer receivingId);

    List<Purchase> findBySupplierCode(String supplierCode);

    List<Purchase> findByPurchaseDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Purchase> findAll();

    List<PurchaseDetail> findDetailsByPurchaseId(Integer purchaseId);

    Purchase findWithDetailsByPurchaseNumber(String purchaseNumber);

    Purchase findByIdWithDetails(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(Purchase purchase);

    int updateWithOptimisticLock(Purchase purchase);

    void updateDetail(PurchaseDetail detail);

    void deleteDetailsByPurchaseId(Integer purchaseId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
