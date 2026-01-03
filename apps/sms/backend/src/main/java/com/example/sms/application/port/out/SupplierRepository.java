package com.example.sms.application.port.out;

import com.example.sms.domain.model.partner.Supplier;

import java.util.List;
import java.util.Optional;

/**
 * 仕入先リポジトリ（Output Port）.
 */
public interface SupplierRepository {

    void save(Supplier supplier);

    Optional<Supplier> findByCodeAndBranch(String supplierCode, String branchNumber);

    List<Supplier> findByCode(String supplierCode);

    List<Supplier> findAll();

    void update(Supplier supplier);

    void deleteByCodeAndBranch(String supplierCode, String branchNumber);

    void deleteAll();
}
