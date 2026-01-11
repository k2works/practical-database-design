package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.Acceptance;

import java.util.List;
import java.util.Optional;

/**
 * 検収データリポジトリ（Output Port）
 */
public interface AcceptanceRepository {

    void save(Acceptance acceptance);

    Optional<Acceptance> findById(Integer id);

    Optional<Acceptance> findByAcceptanceNumber(String acceptanceNumber);

    List<Acceptance> findByInspectionNumber(String inspectionNumber);

    List<Acceptance> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<Acceptance> findAll();

    void deleteAll();
}
