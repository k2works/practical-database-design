package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.Acceptance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AcceptanceMapper {
    void insert(Acceptance acceptance);
    Acceptance findById(Integer id);
    Acceptance findByAcceptanceNumber(String acceptanceNumber);
    List<Acceptance> findByInspectionNumber(String inspectionNumber);
    List<Acceptance> findByPurchaseOrderNumber(String purchaseOrderNumber);
    List<Acceptance> findAll();
    void deleteAll();
}
