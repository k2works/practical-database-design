package com.example.fas.application.service;

import com.example.fas.application.port.in.MonthlyBalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import com.example.fas.domain.model.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 月次残高照会アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyBalanceApplicationService implements MonthlyBalanceUseCase {

    private final MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;

    @Override
    public PageResult<MonthlyBalanceResponse> getMonthlyBalances(
            int page, int size,
            Integer fiscalYear, Integer month,
            String accountCode) {
        PageResult<TrialBalanceLine> pageResult = monthlyAccountBalanceRepository.findWithPagination(
                page, size, fiscalYear, month, accountCode);
        return new PageResult<>(
                pageResult.getContent().stream()
                        .map(MonthlyBalanceResponse::from)
                        .toList(),
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements());
    }
}
