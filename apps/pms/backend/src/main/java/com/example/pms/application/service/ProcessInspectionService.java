package com.example.pms.application.service;

import com.example.pms.application.port.in.ProcessInspectionUseCase;
import com.example.pms.application.port.out.ProcessInspectionRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ProcessInspection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 工程検査（不良管理）サービス（Application Service）.
 */
@Service
@Transactional
public class ProcessInspectionService implements ProcessInspectionUseCase {

    private final ProcessInspectionRepository processInspectionRepository;

    public ProcessInspectionService(ProcessInspectionRepository processInspectionRepository) {
        this.processInspectionRepository = processInspectionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProcessInspection> getProcessInspectionList(int page, int size, String keyword) {
        int offset = page * size;
        List<ProcessInspection> content = processInspectionRepository.findWithPagination(offset, size, keyword);
        long totalElements = processInspectionRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessInspection> getProcessInspection(String inspectionNumber) {
        return processInspectionRepository.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public ProcessInspection createProcessInspection(ProcessInspection inspection) {
        // 検査番号の自動採番
        String inspectionNumber = generateInspectionNumber();
        inspection.setInspectionNumber(inspectionNumber);

        processInspectionRepository.save(inspection);
        return processInspectionRepository.findByInspectionNumber(inspection.getInspectionNumber())
            .orElseThrow(() -> new IllegalStateException("工程検査の登録に失敗しました"));
    }

    @Override
    public ProcessInspection updateProcessInspection(String inspectionNumber, ProcessInspection inspection) {
        ProcessInspection existing = processInspectionRepository.findByInspectionNumber(inspectionNumber)
            .orElseThrow(() -> new IllegalStateException("工程検査が見つかりません: " + inspectionNumber));

        existing.setInspectionQuantity(inspection.getInspectionQuantity());
        existing.setPassedQuantity(inspection.getPassedQuantity());
        existing.setFailedQuantity(inspection.getFailedQuantity());
        existing.setJudgment(inspection.getJudgment());
        existing.setRemarks(inspection.getRemarks());

        processInspectionRepository.update(existing);
        return processInspectionRepository.findByInspectionNumber(inspectionNumber)
            .orElseThrow(() -> new IllegalStateException("工程検査の更新に失敗しました"));
    }

    @Override
    public void deleteProcessInspection(String inspectionNumber) {
        processInspectionRepository.deleteByInspectionNumber(inspectionNumber);
    }

    /**
     * 検査番号を自動採番する.
     *
     * @return 検査番号（PI-yyyyMMdd-NNNN形式）
     */
    private String generateInspectionNumber() {
        String datePrefix = "PI-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<ProcessInspection> allInspections = processInspectionRepository.findAll();

        int maxSeq = allInspections.stream()
            .map(ProcessInspection::getInspectionNumber)
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
