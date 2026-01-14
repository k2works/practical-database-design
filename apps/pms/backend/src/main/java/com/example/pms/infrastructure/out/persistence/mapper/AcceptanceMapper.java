package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.Acceptance;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AcceptanceMapper {
    void insert(Acceptance acceptance);
    Acceptance findById(Integer id);
    Acceptance findByAcceptanceNumber(String acceptanceNumber);
    List<Acceptance> findByInspectionNumber(String inspectionNumber);
    List<Acceptance> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Acceptance> findAll();

    List<Acceptance> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    void update(Acceptance acceptance);

    void deleteByAcceptanceNumber(String acceptanceNumber);

    void deleteAll();
}
