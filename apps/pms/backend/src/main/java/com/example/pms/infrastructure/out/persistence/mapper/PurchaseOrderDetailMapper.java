package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderDetailMapper {
    void insert(PurchaseOrderDetail detail);
    PurchaseOrderDetail findById(Integer id);
    List<PurchaseOrderDetail> findByPurchaseOrderNumber(String purchaseOrderNumber);
    PurchaseOrderDetail findByPurchaseOrderNumberAndLineNumber(
            @Param("purchaseOrderNumber") String purchaseOrderNumber,
            @Param("lineNumber") Integer lineNumber);
    List<PurchaseOrderDetail> findAll();
    void deleteAll();
}
