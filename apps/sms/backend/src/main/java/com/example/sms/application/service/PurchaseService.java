package com.example.sms.application.service;

import com.example.sms.application.port.in.PurchaseUseCase;
import com.example.sms.application.port.in.command.CreatePurchaseCommand;
import com.example.sms.application.port.out.PurchaseRepository;
import com.example.sms.domain.exception.PurchaseNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Purchase;
import com.example.sms.domain.model.purchase.PurchaseDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 仕入アプリケーションサービス.
 */
@Service
@Transactional
public class PurchaseService implements PurchaseUseCase {

    private static final DateTimeFormatter PURCHASE_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Purchase createPurchase(CreatePurchaseCommand command) {
        String purchaseNumber = generatePurchaseNumber();

        List<PurchaseDetail> details = new ArrayList<>();
        int lineNumber = 1;
        for (CreatePurchaseCommand.CreatePurchaseDetailCommand detailCmd : command.details()) {
            PurchaseDetail detail = PurchaseDetail.builder()
                .lineNumber(lineNumber++)
                .productCode(detailCmd.productCode())
                .purchaseQuantity(detailCmd.purchaseQuantity())
                .unitPrice(detailCmd.unitPrice())
                .remarks(detailCmd.remarks())
                .build();
            detail.calculatePurchaseAmount();
            details.add(detail);
        }

        Purchase purchase = Purchase.builder()
            .purchaseNumber(purchaseNumber)
            .receivingId(command.receivingId())
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(command.supplierBranchNumber() != null ? command.supplierBranchNumber() : "00")
            .purchaseDate(command.purchaseDate() != null ? command.purchaseDate() : LocalDate.now())
            .remarks(command.remarks())
            .details(details)
            .build();

        purchase.recalculateTotalAmount();
        purchaseRepository.save(purchase);
        return purchase;
    }

    private String generatePurchaseNumber() {
        String datePrefix = LocalDate.now().format(PURCHASE_NUMBER_FORMAT);
        List<Purchase> todayPurchases = purchaseRepository.findByPurchaseDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayPurchases.size() + 1;
        return String.format("PUR-%s-%04d", datePrefix, sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Purchase> getPurchases(int page, int size, String keyword) {
        return purchaseRepository.findWithPagination(page, size, keyword);
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
