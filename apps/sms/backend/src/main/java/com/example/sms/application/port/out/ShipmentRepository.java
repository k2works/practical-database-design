package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 出荷リポジトリ（Output Port）.
 */
public interface ShipmentRepository {

    void save(Shipment shipment);

    Optional<Shipment> findById(Integer id);

    Optional<Shipment> findByShipmentNumber(String shipmentNumber);

    List<Shipment> findByOrderId(Integer orderId);

    List<Shipment> findByCustomerCode(String customerCode);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByShipmentDateBetween(LocalDate from, LocalDate to);

    List<Shipment> findAll();

    PageResult<Shipment> findWithPagination(int page, int size, String keyword);

    void update(Shipment shipment);

    void deleteById(Integer id);

    void deleteAll();
}
