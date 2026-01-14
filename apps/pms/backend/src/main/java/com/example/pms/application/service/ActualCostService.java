package com.example.pms.application.service;

import com.example.pms.application.port.in.ActualCostUseCase;
import com.example.pms.application.port.out.ActualCostRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.cost.ActualCost;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * 実際原価（製造原価）サービス（Application Service）.
 */
@Service
@Transactional
public class ActualCostService implements ActualCostUseCase {

    private final ActualCostRepository actualCostRepository;

    public ActualCostService(ActualCostRepository actualCostRepository) {
        this.actualCostRepository = actualCostRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ActualCost> getActualCostList(int page, int size, String keyword) {
        int offset = page * size;
        List<ActualCost> content = actualCostRepository.findWithPagination(offset, size, keyword);
        long totalElements = actualCostRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActualCost> getAllActualCosts() {
        return actualCostRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActualCost> getActualCost(String workOrderNumber) {
        return actualCostRepository.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public ActualCost createActualCost(ActualCost actualCost) {
        // 製造原価と単位原価を計算
        calculateCosts(actualCost);

        actualCostRepository.save(actualCost);
        return actualCostRepository.findByWorkOrderNumber(actualCost.getWorkOrderNumber())
            .orElseThrow(() -> new IllegalStateException("製造原価の登録に失敗しました"));
    }

    @Override
    public ActualCost updateActualCost(String workOrderNumber, ActualCost actualCost) {
        ActualCost existing = actualCostRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("製造原価が見つかりません: " + workOrderNumber));

        existing.setItemCode(actualCost.getItemCode());
        existing.setCompletedQuantity(actualCost.getCompletedQuantity());
        existing.setActualMaterialCost(actualCost.getActualMaterialCost());
        existing.setActualLaborCost(actualCost.getActualLaborCost());
        existing.setActualExpense(actualCost.getActualExpense());

        // 製造原価と単位原価を再計算
        calculateCosts(existing);

        actualCostRepository.update(existing);
        return actualCostRepository.findByWorkOrderNumber(workOrderNumber)
            .orElseThrow(() -> new IllegalStateException("製造原価の更新に失敗しました"));
    }

    @Override
    public void deleteActualCost(String workOrderNumber) {
        actualCostRepository.deleteByWorkOrderNumber(workOrderNumber);
    }

    /**
     * 製造原価と単位原価を計算する.
     *
     * @param actualCost 実際原価
     */
    private void calculateCosts(ActualCost actualCost) {
        // 製造原価 = 材料費 + 労務費 + 経費
        BigDecimal manufacturingCost = actualCost.getActualMaterialCost()
            .add(actualCost.getActualLaborCost())
            .add(actualCost.getActualExpense());
        actualCost.setActualManufacturingCost(manufacturingCost);

        // 単位原価 = 製造原価 / 完成数量
        if (actualCost.getCompletedQuantity() != null
                && actualCost.getCompletedQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal unitCost = manufacturingCost.divide(
                actualCost.getCompletedQuantity(), 4, RoundingMode.HALF_UP);
            actualCost.setUnitCost(unitCost);
        } else {
            actualCost.setUnitCost(BigDecimal.ZERO);
        }
    }
}
