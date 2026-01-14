package com.example.pms.application.service;

import com.example.pms.application.port.in.ReceivingUseCase;
import com.example.pms.application.port.out.ReceivingRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 入荷受入サービス.
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
    public PageResult<Receiving> getReceivingList(int page, int size, ReceivingType receivingType, String keyword) {
        int offset = page * size;
        String typeValue = receivingType != null ? receivingType.getDisplayName() : null;
        List<Receiving> content = receivingRepository.findWithPagination(offset, size, typeValue, keyword);
        long totalElements = receivingRepository.count(typeValue, keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receiving> getReceiving(String receivingNumber) {
        return receivingRepository.findByReceivingNumber(receivingNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receiving> getReceivingWithInspections(String receivingNumber) {
        return receivingRepository.findByReceivingNumberWithInspections(receivingNumber);
    }

    @Override
    public Receiving createReceiving(Receiving receiving) {
        // 入荷番号の自動採番
        String receivingNumber = generateReceivingNumber();
        receiving.setReceivingNumber(receivingNumber);
        receiving.setCreatedBy("system");
        receiving.setUpdatedBy("system");

        receivingRepository.save(receiving);
        return receivingRepository.findByReceivingNumber(receivingNumber)
            .orElseThrow(() -> new IllegalStateException("入荷受入の登録に失敗しました"));
    }

    @Override
    public Receiving updateReceiving(String receivingNumber, Receiving receiving) {
        Receiving existing = receivingRepository.findByReceivingNumber(receivingNumber)
            .orElseThrow(() -> new IllegalArgumentException("入荷受入が見つかりません: " + receivingNumber));

        // 更新対象フィールドを設定
        existing.setPurchaseOrderNumber(receiving.getPurchaseOrderNumber());
        existing.setLineNumber(receiving.getLineNumber());
        existing.setReceivingDate(receiving.getReceivingDate());
        existing.setReceiverCode(receiving.getReceiverCode());
        existing.setReceivingType(receiving.getReceivingType());
        existing.setItemCode(receiving.getItemCode());
        existing.setMiscellaneousItemFlag(receiving.getMiscellaneousItemFlag());
        existing.setReceivingQuantity(receiving.getReceivingQuantity());
        existing.setRemarks(receiving.getRemarks());
        existing.setUpdatedBy("system");

        receivingRepository.update(existing);
        return receivingRepository.findByReceivingNumber(receivingNumber)
            .orElseThrow(() -> new IllegalStateException("入荷受入の更新に失敗しました"));
    }

    @Override
    public void deleteReceiving(String receivingNumber) {
        receivingRepository.deleteByReceivingNumber(receivingNumber);
    }

    /**
     * 入荷番号を自動採番する.
     * 形式: RCV-yyyyMMdd-nnnn
     */
    private String generateReceivingNumber() {
        String datePrefix = "RCV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<Receiving> allReceivings = receivingRepository.findAll();

        int maxSeq = allReceivings.stream()
            .map(Receiving::getReceivingNumber)
            .filter(num -> num != null && num.startsWith(datePrefix))
            .map(num -> {
                String seqStr = num.substring(datePrefix.length());
                try {
                    return Integer.parseInt(seqStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max(Integer::compareTo)
            .orElse(0);

        return datePrefix + String.format("%04d", maxSeq + 1);
    }
}
