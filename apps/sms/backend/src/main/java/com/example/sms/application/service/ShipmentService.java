package com.example.sms.application.service;

import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.application.port.in.command.CreateShipmentCommand;
import com.example.sms.application.port.in.command.UpdateShipmentCommand;
import com.example.sms.application.port.out.ShipmentRepository;
import com.example.sms.domain.exception.ShipmentNotFoundException;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 出荷アプリケーションサービス.
 */
@Service
@Transactional
public class ShipmentService implements ShipmentUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final DateTimeFormatter SHIPMENT_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public Shipment createShipment(CreateShipmentCommand command) {
        String shipmentNumber = generateShipmentNumber();

        List<ShipmentDetail> details = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);

        if (command.details() != null) {
            for (CreateShipmentCommand.CreateShipmentDetailCommand detailCmd : command.details()) {
                BigDecimal amount = detailCmd.unitPrice().multiply(detailCmd.shippedQuantity());
                BigDecimal detailTax = amount.multiply(TAX_RATE).setScale(0, RoundingMode.DOWN);

                ShipmentDetail detail = ShipmentDetail.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .orderDetailId(detailCmd.orderDetailId())
                    .productCode(detailCmd.productCode())
                    .productName(detailCmd.productName())
                    .shippedQuantity(detailCmd.shippedQuantity())
                    .unit(detailCmd.unit())
                    .unitPrice(detailCmd.unitPrice())
                    .amount(amount)
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .taxRate(TAX_RATE_PERCENT)
                    .taxAmount(detailTax)
                    .warehouseCode(detailCmd.warehouseCode())
                    .remarks(detailCmd.remarks())
                    .build();

                details.add(detail);
            }
        }

        Shipment shipment = Shipment.builder()
            .shipmentNumber(shipmentNumber)
            .shipmentDate(command.shipmentDate() != null ? command.shipmentDate() : LocalDate.now())
            .orderId(command.orderId())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .shippingDestinationNumber(command.shippingDestinationNumber())
            .shippingDestinationName(command.shippingDestinationName())
            .shippingDestinationPostalCode(command.shippingDestinationPostalCode())
            .shippingDestinationAddress1(command.shippingDestinationAddress1())
            .shippingDestinationAddress2(command.shippingDestinationAddress2())
            .representativeCode(command.representativeCode())
            .warehouseCode(command.warehouseCode())
            .status(ShipmentStatus.INSTRUCTED)
            .remarks(command.remarks())
            .details(details)
            .build();

        shipmentRepository.save(shipment);
        return shipment;
    }

    @Override
    public Shipment updateShipment(String shipmentNumber, UpdateShipmentCommand command) {
        Shipment existing = shipmentRepository.findByShipmentNumber(shipmentNumber)
            .orElseThrow(() -> new ShipmentNotFoundException(shipmentNumber));

        Shipment updated = Shipment.builder()
            .id(existing.getId())
            .shipmentNumber(shipmentNumber)
            .shipmentDate(existing.getShipmentDate())
            .orderId(existing.getOrderId())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(existing.getShippingDestinationNumber())
            .shippingDestinationName(existing.getShippingDestinationName())
            .shippingDestinationPostalCode(existing.getShippingDestinationPostalCode())
            .shippingDestinationAddress1(existing.getShippingDestinationAddress1())
            .shippingDestinationAddress2(existing.getShippingDestinationAddress2())
            .representativeCode(existing.getRepresentativeCode())
            .warehouseCode(existing.getWarehouseCode())
            .status(command.status() != null ? command.status() : existing.getStatus())
            .remarks(command.remarks() != null ? command.remarks() : existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .details(existing.getDetails())
            .build();

        shipmentRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentByNumber(String shipmentNumber) {
        return shipmentRepository.findByShipmentNumber(shipmentNumber)
            .orElseThrow(() -> new ShipmentNotFoundException(shipmentNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> getShipmentsByOrder(Integer orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    @Override
    public Shipment confirmShipment(String shipmentNumber) {
        Shipment existing = shipmentRepository.findByShipmentNumber(shipmentNumber)
            .orElseThrow(() -> new ShipmentNotFoundException(shipmentNumber));

        Shipment confirmed = Shipment.builder()
            .id(existing.getId())
            .shipmentNumber(shipmentNumber)
            .shipmentDate(existing.getShipmentDate())
            .orderId(existing.getOrderId())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(existing.getShippingDestinationNumber())
            .shippingDestinationName(existing.getShippingDestinationName())
            .shippingDestinationPostalCode(existing.getShippingDestinationPostalCode())
            .shippingDestinationAddress1(existing.getShippingDestinationAddress1())
            .shippingDestinationAddress2(existing.getShippingDestinationAddress2())
            .representativeCode(existing.getRepresentativeCode())
            .warehouseCode(existing.getWarehouseCode())
            .status(ShipmentStatus.SHIPPED)
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .details(existing.getDetails())
            .build();

        shipmentRepository.update(confirmed);
        return confirmed;
    }

    @Override
    public void deleteShipment(String shipmentNumber) {
        Shipment existing = shipmentRepository.findByShipmentNumber(shipmentNumber)
            .orElseThrow(() -> new ShipmentNotFoundException(shipmentNumber));

        shipmentRepository.deleteById(existing.getId());
    }

    private String generateShipmentNumber() {
        String datePrefix = LocalDate.now().format(SHIPMENT_NUMBER_FORMAT);
        List<Shipment> todayShipments = shipmentRepository.findByShipmentDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayShipments.size() + 1;
        return String.format("SHP-%s-%04d", datePrefix, sequence);
    }
}
