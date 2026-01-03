package com.example.sms.application.service;

import com.example.sms.application.port.in.ReceivingUseCase;
import com.example.sms.application.port.out.ReceivingRepository;
import com.example.sms.domain.exception.ReceivingNotFoundException;
import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 入荷アプリケーションサービス.
 */
@Service
@Transactional
public class ReceivingService implements ReceivingUseCase {

    private final ReceivingRepository receivingRepository;

    public ReceivingService(ReceivingRepository receivingRepository) {
        this.receivingRepository = receivingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAll();
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
