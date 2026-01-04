package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入荷リポジトリ（Output Port）.
 */
public interface ReceivingRepository {

    void save(Receiving receiving);

    Optional<Receiving> findById(Integer id);

    Optional<Receiving> findByIdWithDetails(Integer id);

    Optional<Receiving> findByReceivingNumber(String receivingNumber);

    Optional<Receiving> findWithDetailsByReceivingNumber(String receivingNumber);

    List<Receiving> findByPurchaseOrderId(Integer purchaseOrderId);

    List<Receiving> findBySupplierCode(String supplierCode);

    List<Receiving> findByStatus(ReceivingStatus status);

    List<Receiving> findByReceivingDateBetween(LocalDate from, LocalDate to);

    List<Receiving> findAll();

    PageResult<Receiving> findWithPagination(int page, int size, String keyword);

    void update(Receiving receiving);

    void deleteById(Integer id);

    void deleteAll();
}
