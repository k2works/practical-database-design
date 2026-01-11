package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.supplier.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface SupplierMapper {
    void insert(Supplier supplier);
    Optional<Supplier> findBySupplierCode(String supplierCode);
    Optional<Supplier> findBySupplierCodeAndDate(@Param("supplierCode") String supplierCode,
                                                   @Param("baseDate") LocalDate baseDate);
    List<Supplier> findAll();
    void update(Supplier supplier);
    void deleteAll();
}
