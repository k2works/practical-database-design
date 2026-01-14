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

    /**
     * 入荷番号で検索（検査を含む）.
     */
    Receiving findByReceivingNumberWithInspections(String receivingNumber);

    List<Receiving> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Receiving> findByPurchaseOrderNumberAndLineNumber(
            @Param("purchaseOrderNumber") String purchaseOrderNumber,
            @Param("lineNumber") Integer lineNumber);
    List<Receiving> findAll();

    List<Receiving> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("receivingType") String receivingType,
            @Param("keyword") String keyword);

    long count(
            @Param("receivingType") String receivingType,
            @Param("keyword") String keyword);

    void update(Receiving receiving);

    void deleteByReceivingNumber(String receivingNumber);

    void deleteAll();
}
