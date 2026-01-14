package com.example.pms.application.service;

import com.example.pms.application.port.in.CostVarianceUseCase;
import com.example.pms.application.port.out.CostVarianceRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.CostVariance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 原価差異サービス（Application Service）.
 */
@Service
@Transactional
public class CostVarianceService implements CostVarianceUseCase {

    private final CostVarianceRepository costVarianceRepository;

    public CostVarianceService(CostVarianceRepository costVarianceRepository) {
        this.costVarianceRepository = costVarianceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CostVariance> getCostVarianceList(int page, int size, String keyword) {
        int offset = page * size;
        List<CostVariance> content = costVarianceRepository.findWithPagination(offset, size, keyword);
        long totalElements = costVarianceRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CostVariance> getAllCostVariances() {
        return costVarianceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CostVariance> getCostVariance(String workOrderNumber) {
        return costVarianceRepository.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public CostVariance createCostVariance(CostVariance costVariance) {
        // 総差異を計算
        calculateTotalVariance(costVariance);

        costVarianceRepository.save(costVariance);
        return costVarianceRepository.findByWorkOrderNumber(costVariance.getWorkOrderNumber())
            .orElseThrow(() -> new IllegalStateException("原価差異の登録に失敗しました"));
    }

    @Override
    public CostVariance updateCostVariance(String workOrderNumber, CostVariance costVariance) {
        CostVariance existing = costVarianceRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("原価差異が見つかりません: " + workOrderNumber));

        existing.setItemCode(costVariance.getItemCode());
        existing.setMaterialCostVariance(costVariance.getMaterialCostVariance());
        existing.setLaborCostVariance(costVariance.getLaborCostVariance());
        existing.setExpenseVariance(costVariance.getExpenseVariance());

        // 総差異を再計算
        calculateTotalVariance(existing);

        costVarianceRepository.update(existing);
        return costVarianceRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("原価差異の更新に失敗しました"));
    }

    @Override
    public void deleteCostVariance(String workOrderNumber) {
        costVarianceRepository.deleteByWorkOrderNumber(workOrderNumber);
    }

    /**
     * 総差異を計算する.
     *
     * @param costVariance 原価差異
     */
    private void calculateTotalVariance(CostVariance costVariance) {
        BigDecimal materialVariance = costVariance.getMaterialCostVariance() != null
            ? costVariance.getMaterialCostVariance() : BigDecimal.ZERO;
        BigDecimal laborVariance = costVariance.getLaborCostVariance() != null
            ? costVariance.getLaborCostVariance() : BigDecimal.ZERO;
        BigDecimal expenseVariance = costVariance.getExpenseVariance() != null
            ? costVariance.getExpenseVariance() : BigDecimal.ZERO;

        BigDecimal totalVariance = materialVariance.add(laborVariance).add(expenseVariance);
        costVariance.setTotalVariance(totalVariance);
    }
}
