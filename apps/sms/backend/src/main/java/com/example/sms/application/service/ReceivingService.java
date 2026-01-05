package com.example.sms.application.service;

import com.example.sms.application.port.in.ReceivingUseCase;
import com.example.sms.application.port.in.command.CreateReceivingCommand;
import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.domain.exception.ReceivingNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingDetail;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 入荷アプリケーションサービス.
 */
@Service
@Transactional
public class ReceivingService implements ReceivingUseCase {

    private static final DateTimeFormatter RECEIVING_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReceivingRepository receivingRepository;

    public ReceivingService(ReceivingRepository receivingRepository) {
        this.receivingRepository = receivingRepository;
    }

    @Override
    public Receiving createReceiving(CreateReceivingCommand command) {
        String receivingNumber = generateReceivingNumber();

        List<ReceivingDetail> details = new ArrayList<>();
        int lineNumber = 1;
        for (CreateReceivingCommand.CreateReceivingDetailCommand detailCmd : command.details()) {
            BigDecimal amount = detailCmd.receivingQuantity().multiply(detailCmd.unitPrice());
            ReceivingDetail detail = ReceivingDetail.builder()
                .lineNumber(lineNumber++)
                .purchaseOrderDetailId(detailCmd.purchaseOrderDetailId())
                .productCode(detailCmd.productCode())
                .receivingQuantity(detailCmd.receivingQuantity())
                .inspectedQuantity(BigDecimal.ZERO)
                .acceptedQuantity(BigDecimal.ZERO)
                .rejectedQuantity(BigDecimal.ZERO)
                .unitPrice(detailCmd.unitPrice())
                .amount(amount)
                .remarks(detailCmd.remarks())
                .build();
            details.add(detail);
        }

        Receiving receiving = Receiving.builder()
            .receivingNumber(receivingNumber)
            .purchaseOrderId(command.purchaseOrderId())
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(command.supplierBranchNumber() != null ? command.supplierBranchNumber() : "00")
            .receivingDate(command.receivingDate() != null ? command.receivingDate() : LocalDate.now())
            .status(ReceivingStatus.WAITING)
            .receiverCode(command.receiverCode())
            .warehouseCode(command.warehouseCode())
            .remarks(command.remarks())
            .details(details)
            .build();

        receivingRepository.save(receiving);
        return receiving;
    }

    private String generateReceivingNumber() {
        String datePrefix = LocalDate.now().format(RECEIVING_NUMBER_FORMAT);
        List<Receiving> todayReceivings = receivingRepository.findByReceivingDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayReceivings.size() + 1;
        return String.format("RCV-%s-%04d", datePrefix, sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Receiving> getReceivings(int page, int size, String keyword) {
        return receivingRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Receiving getReceivingByNumber(String receivingNumber) {
        return receivingRepository.findByReceivingNumber(receivingNumber)
            .orElseThrow(() -> new ReceivingNotFoundException(receivingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Receiving getReceivingWithDetails(String receivingNumber) {
        return receivingRepository.findWithDetailsByReceivingNumber(receivingNumber)
            .orElseThrow(() -> new ReceivingNotFoundException(receivingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getReceivingsByStatus(ReceivingStatus status) {
        return receivingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getReceivingsBySupplier(String supplierCode) {
        return receivingRepository.findBySupplierCode(supplierCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getReceivingsByPurchaseOrder(Integer purchaseOrderId) {
        return receivingRepository.findByPurchaseOrderId(purchaseOrderId);
    }
}
