package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入荷マッパー.
 */
@Mapper
public interface ReceivingMapper {

    void insertHeader(Receiving receiving);

    void insertDetail(ReceivingDetail detail);

    Optional<Receiving> findById(Integer id);

    Optional<Receiving> findByReceivingNumber(String receivingNumber);

    List<Receiving> findByPurchaseOrderId(Integer purchaseOrderId);

    List<Receiving> findBySupplierCode(String supplierCode);

    List<Receiving> findByStatus(@Param("status") ReceivingStatus status);

    List<Receiving> findByReceivingDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Receiving> findAll();

    List<ReceivingDetail> findDetailsByReceivingId(Integer receivingId);

    Receiving findWithDetailsByReceivingNumber(String receivingNumber);

    Receiving findByIdWithDetails(Integer id);

    Integer findVersionById(Integer id);

    void updateHeader(Receiving receiving);

    int updateWithOptimisticLock(Receiving receiving);

    void updateDetail(ReceivingDetail detail);

    void deleteDetailsByReceivingId(Integer receivingId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
