package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.subcontract.Supply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplyMapper {
    void insert(Supply supply);
    Supply findById(Integer id);
    Supply findBySupplyNumber(String supplyNumber);

    /**
     * 支給番号で検索（明細を含む）.
     */
    Supply findBySupplyNumberWithDetails(String supplyNumber);

    List<Supply> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Supply> findByPurchaseOrderNumberAndLineNumber(
            @Param("purchaseOrderNumber") String purchaseOrderNumber,
            @Param("lineNumber") Integer lineNumber);
    List<Supply> findBySupplierCode(String supplierCode);
    List<Supply> findAll();
    void deleteAll();
}
