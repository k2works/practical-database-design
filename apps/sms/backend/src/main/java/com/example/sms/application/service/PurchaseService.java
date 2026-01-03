package com.example.sms.application.service;

import com.example.sms.application.port.in.PurchaseUseCase;
import com.example.sms.application.port.out.PurchaseRepository;
import com.example.sms.domain.exception.PurchaseNotFoundException;
import com.example.sms.domain.model.purchase.Purchase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仕入アプリケーションサービス.
 */
@Service
@Transactional
public class PurchaseService implements PurchaseUseCase {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Purchase getPurchaseByNumber(String purchaseNumber) {
        return purchaseRepository.findByPurchaseNumber(purchaseNumber)
            .orElseThrow(() -> new PurchaseNotFoundException(purchaseNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Purchase getPurchaseWithDetails(String purchaseNumber) {
        return purchaseRepository.findWithDetailsByPurchaseNumber(purchaseNumber)
            .orElseThrow(() -> new PurchaseNotFoundException(purchaseNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesBySupplier(String supplierCode) {
        return purchaseRepository.findBySupplierCode(supplierCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByReceiving(Integer receivingId) {
        return purchaseRepository.findByReceivingId(receivingId);
    }
}
