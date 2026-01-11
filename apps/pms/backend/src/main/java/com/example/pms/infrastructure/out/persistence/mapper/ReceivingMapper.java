package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.Receiving;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReceivingMapper {
    void insert(Receiving receiving);
    Receiving findById(Integer id);
    Receiving findByReceivingNumber(String receivingNumber);
    List<Receiving> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Receiving> findByPurchaseOrderNumberAndLineNumber(
            @Param("purchaseOrderNumber") String purchaseOrderNumber,
            @Param("lineNumber") Integer lineNumber);
    List<Receiving> findAll();
    void deleteAll();
}
