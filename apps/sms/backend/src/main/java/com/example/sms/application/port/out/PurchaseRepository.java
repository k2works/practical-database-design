package com.example.sms.application.port.out;

import com.example.sms.domain.model.purchase.Purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 仕入リポジトリ（Output Port）.
 */
public interface PurchaseRepository {

    void save(Purchase purchase);

    Optional<Purchase> findById(Integer id);

    Optional<Purchase> findByIdWithDetails(Integer id);

    Optional<Purchase> findByPurchaseNumber(String purchaseNumber);

    Optional<Purchase> findWithDetailsByPurchaseNumber(String purchaseNumber);

    List<Purchase> findByReceivingId(Integer receivingId);

    List<Purchase> findBySupplierCode(String supplierCode);

    List<Purchase> findByPurchaseDateBetween(LocalDate from, LocalDate to);

    List<Purchase> findAll();

    void update(Purchase purchase);

    void deleteById(Integer id);

    void deleteAll();
}
