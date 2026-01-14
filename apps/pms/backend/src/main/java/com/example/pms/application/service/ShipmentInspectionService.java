package com.example.pms.application.service;

import com.example.pms.application.port.in.ShipmentInspectionUseCase;
import com.example.pms.application.port.out.ShipmentInspectionRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ShipmentInspection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 出荷検査実績サービス（Application Service）.
 */
@Service
@Transactional
public class ShipmentInspectionService implements ShipmentInspectionUseCase {

    private final ShipmentInspectionRepository shipmentInspectionRepository;

    public ShipmentInspectionService(ShipmentInspectionRepository shipmentInspectionRepository) {
        this.shipmentInspectionRepository = shipmentInspectionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ShipmentInspection> getShipmentInspectionList(int page, int size, String keyword) {
        int offset = page * size;
        List<ShipmentInspection> content = shipmentInspectionRepository.findWithPagination(offset, size, keyword);
        long totalElements = shipmentInspectionRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipmentInspection> getShipmentInspection(String inspectionNumber) {
        return shipmentInspectionRepository.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public ShipmentInspection createShipmentInspection(ShipmentInspection inspection) {
        // 検査番号の自動採番
        String inspectionNumber = generateInspectionNumber();
        inspection.setInspectionNumber(inspectionNumber);

        shipmentInspectionRepository.save(inspection);
        return shipmentInspectionRepository.findByInspectionNumber(inspection.getInspectionNumber())
            .orElseThrow(() -> new IllegalStateException("出荷検査実績の登録に失敗しました"));
    }

    @Override
    public ShipmentInspection updateShipmentInspection(String inspectionNumber, ShipmentInspection inspection) {
        ShipmentInspection existing = shipmentInspectionRepository.findByInspectionNumber(inspectionNumber)
            .orElseThrow(() -> new IllegalStateException("出荷検査実績が見つかりません: " + inspectionNumber));

        existing.setInspectionQuantity(inspection.getInspectionQuantity());
        existing.setPassedQuantity(inspection.getPassedQuantity());
        existing.setFailedQuantity(inspection.getFailedQuantity());
        existing.setJudgment(inspection.getJudgment());
        existing.setRemarks(inspection.getRemarks());

        shipmentInspectionRepository.update(existing);
        return shipmentInspectionRepository.findByInspectionNumber(inspectionNumber)
            .orElseThrow(() -> new IllegalStateException("出荷検査実績の更新に失敗しました"));
    }

    @Override
    public void deleteShipmentInspection(String inspectionNumber) {
        shipmentInspectionRepository.deleteByInspectionNumber(inspectionNumber);
    }

    /**
     * 検査番号を自動採番する.
     *
     * @return 検査番号（SI-yyyyMMdd-NNNN形式）
     */
    private String generateInspectionNumber() {
        String datePrefix = "SI-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<ShipmentInspection> allInspections = shipmentInspectionRepository.findAll();

        int maxSeq = allInspections.stream()
            .map(ShipmentInspection::getInspectionNumber)
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
