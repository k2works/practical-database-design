package com.example.pms.application.port.out;

import com.example.pms.domain.model.supplier.Supplier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 取引先マスタリポジトリインターフェース.
 */
public interface SupplierRepository {

    void save(Supplier supplier);

    Optional<Supplier> findBySupplierCode(String supplierCode);

    Optional<Supplier> findBySupplierCodeAndDate(String supplierCode, LocalDate baseDate);

    List<Supplier> findAll();

    void update(Supplier supplier);

    void deleteAll();
}
