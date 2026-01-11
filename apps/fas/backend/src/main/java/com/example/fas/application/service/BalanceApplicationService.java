package com.example.fas.application.service;

import com.example.fas.application.port.in.BalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse.TrialBalanceLineResponse;
import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 残高照会アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceApplicationService implements BalanceUseCase {

    private final MonthlyAccountBalanceRepository monthlyAccountBalanceRepository;

    @Override
    public TrialBalanceResponse getTrialBalance(Integer fiscalYear, Integer month) {
        List<TrialBalanceLine> lines =
                monthlyAccountBalanceRepository.getTrialBalance(fiscalYear, month);
        return buildTrialBalanceResponse(fiscalYear, month, lines);
    }

    @Override
    public TrialBalanceResponse getTrialBalanceByBsPlType(
            Integer fiscalYear, Integer month, String bsPlType) {
        List<TrialBalanceLine> lines =
                monthlyAccountBalanceRepository.getTrialBalanceByBSPL(fiscalYear, month, bsPlType);
        return buildTrialBalanceResponse(fiscalYear, month, lines);
    }

    @Override
    public List<MonthlyBalanceResponse> getMonthlyBalances(Integer fiscalYear, Integer month) {
        return monthlyAccountBalanceRepository.findByFiscalYearAndMonth(fiscalYear, month).stream()
                .map(MonthlyBalanceResponse::from)
                .toList();
    }

    @Override
    public List<MonthlyBalanceResponse> getMonthlyBalancesByAccountCode(
            Integer fiscalYear, String accountCode) {
        return monthlyAccountBalanceRepository.findByAccountCode(fiscalYear, accountCode).stream()
                .map(MonthlyBalanceResponse::from)
                .toList();
    }

    private TrialBalanceResponse buildTrialBalanceResponse(
            Integer fiscalYear, Integer month, List<TrialBalanceLine> lines) {
        List<TrialBalanceLineResponse> lineResponses = lines.stream()
                .map(TrialBalanceLineResponse::from)
                .toList();

        BigDecimal totalDebit = lines.stream()
                .map(TrialBalanceLine::getDebitTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(TrialBalanceLine::getCreditTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TrialBalanceResponse.builder()
                .fiscalYear(fiscalYear)
                .month(month)
                .lines(lineResponses)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .build();
    }
}
