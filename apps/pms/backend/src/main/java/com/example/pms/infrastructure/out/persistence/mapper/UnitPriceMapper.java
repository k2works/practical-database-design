package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.unitprice.UnitPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UnitPriceMapper {
    void insert(UnitPrice unitPrice);
    Optional<UnitPrice> findByItemCodeAndSupplierCode(@Param("itemCode") String itemCode,
                                                       @Param("supplierCode") String supplierCode);
    Optional<UnitPrice> findByItemCodeAndSupplierCodeAndDate(@Param("itemCode") String itemCode,
                                                              @Param("supplierCode") String supplierCode,
                                                              @Param("baseDate") LocalDate baseDate);
    List<UnitPrice> findByItemCode(String itemCode);
    List<UnitPrice> findAll();
    void update(UnitPrice unitPrice);
    void deleteAll();
}
