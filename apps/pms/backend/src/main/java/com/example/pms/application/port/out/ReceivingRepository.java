package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.Receiving;

import java.util.List;
import java.util.Optional;

/**
 * 入荷受入データリポジトリ（Output Port）
 */
public interface ReceivingRepository {

    void save(Receiving receiving);

    Optional<Receiving> findById(Integer id);

    Optional<Receiving> findByReceivingNumber(String receivingNumber);

    List<Receiving> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<Receiving> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber);

    List<Receiving> findAll();

    void deleteAll();
}
