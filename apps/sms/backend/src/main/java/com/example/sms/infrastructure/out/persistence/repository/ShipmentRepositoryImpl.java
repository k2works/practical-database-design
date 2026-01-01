package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ShipmentRepository;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import com.example.sms.infrastructure.out.persistence.mapper.ShipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 出荷リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepository {

    private final ShipmentMapper shipmentMapper;

    @Override
    public void save(Shipment shipment) {
        shipmentMapper.insertHeader(shipment);
        if (shipment.getDetails() != null) {
            for (ShipmentDetail detail : shipment.getDetails()) {
                detail.setShipmentId(shipment.getId());
                shipmentMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Shipment> findById(Integer id) {
        return shipmentMapper.findById(id);
    }

    @Override
    public Optional<Shipment> findByShipmentNumber(String shipmentNumber) {
        return shipmentMapper.findByShipmentNumber(shipmentNumber);
    }

    @Override
    public List<Shipment> findByOrderId(Integer orderId) {
        return shipmentMapper.findByOrderId(orderId);
    }

    @Override
    public List<Shipment> findByCustomerCode(String customerCode) {
        return shipmentMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentMapper.findByStatus(status);
    }

    @Override
    public List<Shipment> findByShipmentDateBetween(LocalDate from, LocalDate to) {
        return shipmentMapper.findByShipmentDateBetween(from, to);
    }

    @Override
    public List<Shipment> findAll() {
        return shipmentMapper.findAll();
    }

    @Override
    public void update(Shipment shipment) {
        shipmentMapper.updateHeader(shipment);
        shipmentMapper.deleteDetailsByShipmentId(shipment.getId());
        if (shipment.getDetails() != null) {
            for (ShipmentDetail detail : shipment.getDetails()) {
                detail.setShipmentId(shipment.getId());
                shipmentMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        shipmentMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        shipmentMapper.deleteAll();
    }
}
