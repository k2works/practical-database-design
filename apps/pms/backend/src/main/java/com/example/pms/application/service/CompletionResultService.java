package com.example.pms.application.service;

import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.out.CompletionResultRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.CompletionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 完成実績サービス（Application Service）.
 */
@Service
@Transactional
public class CompletionResultService implements CompletionResultUseCase {

    private final CompletionResultRepository completionResultRepository;

    public CompletionResultService(CompletionResultRepository completionResultRepository) {
        this.completionResultRepository = completionResultRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CompletionResult> getCompletionResultList(int page, int size, String keyword) {
        int offset = page * size;
        List<CompletionResult> content = completionResultRepository.findWithPagination(offset, size, keyword);
        long totalElements = completionResultRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompletionResult> getAllCompletionResults() {
        return completionResultRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompletionResult> getCompletionResult(String completionResultNumber) {
        return completionResultRepository.findByCompletionResultNumber(completionResultNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompletionResult> getCompletionResultsByWorkOrder(String workOrderNumber) {
        return completionResultRepository.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public CompletionResult createCompletionResult(CompletionResult completionResult) {
        String completionResultNumber = generateCompletionResultNumber();
        completionResult.setCompletionResultNumber(completionResultNumber);
        completionResult.setCreatedBy("system");
        completionResult.setUpdatedBy("system");

        if (completionResult.getCompletedQuantity() == null) {
            completionResult.setCompletedQuantity(BigDecimal.ZERO);
        }
        if (completionResult.getGoodQuantity() == null) {
            completionResult.setGoodQuantity(BigDecimal.ZERO);
        }
        if (completionResult.getDefectQuantity() == null) {
            completionResult.setDefectQuantity(BigDecimal.ZERO);
        }
        if (completionResult.getVersion() == null) {
            completionResult.setVersion(1);
        }

        completionResultRepository.save(completionResult);
        return completionResultRepository.findByCompletionResultNumber(completionResultNumber)
            .orElseThrow(() -> new IllegalStateException("完成実績の登録に失敗しました"));
    }

    @Override
    public CompletionResult updateCompletionResult(String completionResultNumber, CompletionResult completionResult) {
        CompletionResult existing = completionResultRepository.findByCompletionResultNumber(completionResultNumber)
            .orElseThrow(() -> new IllegalStateException("完成実績が見つかりません: " + completionResultNumber));

        existing.setWorkOrderNumber(completionResult.getWorkOrderNumber());
        existing.setItemCode(completionResult.getItemCode());
        existing.setCompletionDate(completionResult.getCompletionDate());
        existing.setCompletedQuantity(completionResult.getCompletedQuantity());
        existing.setGoodQuantity(completionResult.getGoodQuantity());
        existing.setDefectQuantity(completionResult.getDefectQuantity());
        existing.setRemarks(completionResult.getRemarks());
        existing.setUpdatedBy("system");

        completionResultRepository.update(existing);
        return completionResultRepository.findByCompletionResultNumber(completionResultNumber)
            .orElseThrow(() -> new IllegalStateException("完成実績の更新に失敗しました"));
    }

    @Override
    public void deleteCompletionResult(String completionResultNumber) {
        completionResultRepository.deleteByCompletionResultNumber(completionResultNumber);
    }

    private String generateCompletionResultNumber() {
        String datePrefix = "CR-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<CompletionResult> allCompletionResults = completionResultRepository.findAll();

        int maxSeq = allCompletionResults.stream()
            .map(CompletionResult::getCompletionResultNumber)
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
