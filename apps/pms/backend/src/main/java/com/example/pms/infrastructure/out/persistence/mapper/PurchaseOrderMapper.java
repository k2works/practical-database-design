package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    void insert(PurchaseOrder purchaseOrder);
    PurchaseOrder findById(Integer id);
    PurchaseOrder findByPurchaseOrderNumber(String purchaseOrderNumber);

    /**
     * 発注番号で検索（明細を含む）.
     */
    PurchaseOrder findByPurchaseOrderNumberWithDetails(String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplierCode(String supplierCode);
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findAll();
    void updateStatus(@Param("id") Integer id, @Param("status") PurchaseOrderStatus status);
    void deleteAll();
}
