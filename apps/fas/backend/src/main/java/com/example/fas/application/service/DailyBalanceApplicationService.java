package com.example.fas.application.service;

import com.example.fas.application.port.in.DailyBalanceUseCase;
import com.example.fas.application.port.in.dto.DailyBalanceResponse;
import com.example.fas.application.port.out.DailyAccountBalanceRepository;
import com.example.fas.domain.model.balance.DailyReportLine;
import com.example.fas.domain.model.common.PageResult;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日次残高照会アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyBalanceApplicationService implements DailyBalanceUseCase {

    private final DailyAccountBalanceRepository dailyAccountBalanceRepository;

    @Override
    public PageResult<DailyBalanceResponse> getDailyBalances(
            int page, int size,
            LocalDate fromDate, LocalDate toDate,
            String accountCode) {
        PageResult<DailyReportLine> pageResult = dailyAccountBalanceRepository.findWithPagination(
                page, size, fromDate, toDate, accountCode);
        return new PageResult<>(
                pageResult.getContent().stream()
                        .map(DailyBalanceResponse::from)
                        .toList(),
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements());
    }

    @Override
    public PageResult<DailyBalanceResponse> getDailyBalancesByDate(
            LocalDate postingDate, int page, int size) {
        PageResult<DailyReportLine> pageResult = dailyAccountBalanceRepository
                .findByPostingDateWithPagination(postingDate, page, size);
        return new PageResult<>(
                pageResult.getContent().stream()
                        .map(DailyBalanceResponse::from)
                        .toList(),
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements());
    }
}
