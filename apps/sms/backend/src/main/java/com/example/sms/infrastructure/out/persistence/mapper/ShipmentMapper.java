package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 出荷マッパー.
 */
@Mapper
public interface ShipmentMapper {

    void insertHeader(Shipment shipment);

    void insertDetail(ShipmentDetail detail);

    Optional<Shipment> findById(Integer id);

    Optional<Shipment> findByShipmentNumber(String shipmentNumber);

    List<Shipment> findByOrderId(Integer orderId);

    List<Shipment> findByCustomerCode(String customerCode);

    List<Shipment> findByStatus(@Param("status") ShipmentStatus status);

    List<Shipment> findByShipmentDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Shipment> findAll();

    List<ShipmentDetail> findDetailsByShipmentId(Integer shipmentId);

    void updateHeader(Shipment shipment);

    void deleteDetailsByShipmentId(Integer shipmentId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
