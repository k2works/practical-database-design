package com.example.pms.application.service;

import com.example.pms.application.port.in.InspectionResultUseCase;
import com.example.pms.application.port.out.InspectionResultRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.InspectionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 検査実績サービス（Application Service）.
 */
@Service
@Transactional
public class InspectionResultService implements InspectionResultUseCase {

    private final InspectionResultRepository inspectionResultRepository;

    public InspectionResultService(InspectionResultRepository inspectionResultRepository) {
        this.inspectionResultRepository = inspectionResultRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<InspectionResult> getInspectionResultList(int page, int size, String keyword) {
        int offset = page * size;
        List<InspectionResult> content = inspectionResultRepository.findWithPagination(offset, size, keyword);
        long totalElements = inspectionResultRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspectionResult> getAllInspectionResults() {
        return inspectionResultRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InspectionResult> getInspectionResult(Integer id) {
        return inspectionResultRepository.findById(id);
    }

    @Override
    public InspectionResult createInspectionResult(InspectionResult inspectionResult) {
        inspectionResult.setCreatedBy("system");
        inspectionResult.setUpdatedBy("system");
        inspectionResultRepository.save(inspectionResult);
        return inspectionResultRepository.findById(inspectionResult.getId())
            .orElseThrow(() -> new IllegalStateException("検査実績の登録に失敗しました"));
    }

    @Override
    public InspectionResult updateInspectionResult(Integer id, InspectionResult inspectionResult) {
        InspectionResult existing = inspectionResultRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("検査実績が見つかりません: " + id));

        existing.setCompletionResultNumber(inspectionResult.getCompletionResultNumber());
        existing.setDefectCode(inspectionResult.getDefectCode());
        existing.setQuantity(inspectionResult.getQuantity());
        existing.setUpdatedBy("system");

        inspectionResultRepository.save(existing);
        return inspectionResultRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("検査実績の更新に失敗しました"));
    }

    @Override
    public void deleteInspectionResult(Integer id) {
        inspectionResultRepository.deleteById(id);
    }
}
