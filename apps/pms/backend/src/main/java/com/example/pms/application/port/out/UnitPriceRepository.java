package com.example.pms.application.port.out;

import com.example.pms.domain.model.unitprice.UnitPrice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 単価マスタリポジトリインターフェース.
 */
public interface UnitPriceRepository {
    void save(UnitPrice unitPrice);
    Optional<UnitPrice> findByItemCodeAndSupplierCode(String itemCode, String supplierCode);
    Optional<UnitPrice> findByItemCodeAndSupplierCodeAndDate(String itemCode, String supplierCode, LocalDate baseDate);
    List<UnitPrice> findByItemCode(String itemCode);
    List<UnitPrice> findAll();
    void update(UnitPrice unitPrice);
    void deleteAll();
}
