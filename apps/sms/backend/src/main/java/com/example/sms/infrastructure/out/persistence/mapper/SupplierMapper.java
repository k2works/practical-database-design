package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.partner.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 仕入先マッパー.
 */
@Mapper
public interface SupplierMapper {

    void insert(Supplier supplier);

    Optional<Supplier> findByCodeAndBranch(@Param("supplierCode") String supplierCode,
                                           @Param("branchNumber") String branchNumber);

    List<Supplier> findByCode(@Param("supplierCode") String supplierCode);

    List<Supplier> findAll();

    void update(Supplier supplier);

    void deleteByCodeAndBranch(@Param("supplierCode") String supplierCode,
                               @Param("branchNumber") String branchNumber);

    void deleteAll();
}
