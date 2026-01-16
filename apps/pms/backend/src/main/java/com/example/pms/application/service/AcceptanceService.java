package com.example.pms.application.service;

import com.example.pms.application.port.in.AcceptanceUseCase;
import com.example.pms.application.port.out.AcceptanceRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Acceptance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 検収サービス.
 */
@Service
@Transactional
public class AcceptanceService implements AcceptanceUseCase {

    private final AcceptanceRepository acceptanceRepository;

    public AcceptanceService(AcceptanceRepository acceptanceRepository) {
        this.acceptanceRepository = acceptanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Acceptance> getAcceptanceList(int page, int size, String keyword) {
        int offset = page * size;
        List<Acceptance> content = acceptanceRepository.findWithPagination(offset, size, keyword);
        long totalElements = acceptanceRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Acceptance> getAllAcceptances() {
        return acceptanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Acceptance> getAcceptance(String acceptanceNumber) {
        return acceptanceRepository.findByAcceptanceNumber(acceptanceNumber);
    }

    @Override
    public Acceptance createAcceptance(Acceptance acceptance) {
        // 検収番号の自動採番
        String acceptanceNumber = generateAcceptanceNumber();
        acceptance.setAcceptanceNumber(acceptanceNumber);
        acceptance.setCreatedBy("system");
        acceptance.setUpdatedBy("system");

        acceptanceRepository.save(acceptance);
        return acceptanceRepository.findByAcceptanceNumber(acceptanceNumber)
            .orElseThrow(() -> new IllegalStateException("検収の登録に失敗しました"));
    }

    @Override
    public Acceptance updateAcceptance(String acceptanceNumber, Acceptance acceptance) {
        Acceptance existing = acceptanceRepository.findByAcceptanceNumber(acceptanceNumber)
            .orElseThrow(() -> new IllegalArgumentException("検収が見つかりません: " + acceptanceNumber));

        // 更新対象フィールドを設定
        existing.setInspectionNumber(acceptance.getInspectionNumber());
        existing.setPurchaseOrderNumber(acceptance.getPurchaseOrderNumber());
        existing.setLineNumber(acceptance.getLineNumber());
        existing.setAcceptanceDate(acceptance.getAcceptanceDate());
        existing.setAcceptorCode(acceptance.getAcceptorCode());
        existing.setSupplierCode(acceptance.getSupplierCode());
        existing.setItemCode(acceptance.getItemCode());
        existing.setMiscellaneousItemFlag(acceptance.getMiscellaneousItemFlag());
        existing.setAcceptedQuantity(acceptance.getAcceptedQuantity());
        existing.setUnitPrice(acceptance.getUnitPrice());
        existing.setAmount(acceptance.getAmount());
        existing.setTaxAmount(acceptance.getTaxAmount());
        existing.setRemarks(acceptance.getRemarks());
        existing.setUpdatedBy("system");

        acceptanceRepository.update(existing);
        return acceptanceRepository.findByAcceptanceNumber(acceptanceNumber)
            .orElseThrow(() -> new IllegalStateException("検収の更新に失敗しました"));
    }

    @Override
    public void deleteAcceptance(String acceptanceNumber) {
        acceptanceRepository.deleteByAcceptanceNumber(acceptanceNumber);
    }

    /**
     * 検収番号を自動採番する.
     * 形式: ACC-yyyyMMdd-nnnn
     */
    private String generateAcceptanceNumber() {
        String datePrefix = "ACC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<Acceptance> allAcceptances = acceptanceRepository.findAll();

        int maxSeq = allAcceptances.stream()
            .map(Acceptance::getAcceptanceNumber)
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
