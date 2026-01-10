package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.infrastructure.out.persistence.mapper.MonthlyAccountBalanceMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 月次勘定科目残高リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class MonthlyAccountBalanceRepositoryImpl implements MonthlyAccountBalanceRepository {

    private final MonthlyAccountBalanceMapper mapper;

    @Override
    @Transactional
    public void save(MonthlyAccountBalance balance) {
        mapper.insert(balance);
    }

    @Override
    public Optional<MonthlyAccountBalance> findByKey(MonthlyAccountBalance.CompositeKey key) {
        return Optional.ofNullable(mapper.findByKey(
                key.getFiscalYear(),
                key.getMonth(),
                key.getAccountCode(),
                key.getSubAccountCode(),
                key.getDepartmentCode(),
                key.getProjectCode(),
                key.getClosingJournalFlag()));
    }

    @Override
    public List<MonthlyAccountBalance> findByFiscalYearAndMonth(
            Integer fiscalYear, Integer month) {
        return mapper.findByFiscalYearAndMonth(fiscalYear, month);
    }

    @Override
    public List<MonthlyAccountBalance> findByAccountCode(
            Integer fiscalYear, String accountCode) {
        return mapper.findByAccountCode(fiscalYear, accountCode);
    }

    @Override
    public List<TrialBalanceLine> getTrialBalance(Integer fiscalYear, Integer month) {
        return mapper.getTrialBalance(fiscalYear, month);
    }

    @Override
    public List<TrialBalanceLine> getTrialBalanceByBSPL(
            Integer fiscalYear, Integer month, String bsplType) {
        return mapper.getTrialBalanceByBSPL(fiscalYear, month, bsplType);
    }

    @Override
    @Transactional
    public int aggregateFromDaily(Integer fiscalYear, Integer month,
            LocalDate fromDate, LocalDate toDate) {
        return mapper.aggregateFromDaily(fiscalYear, month, fromDate, toDate);
    }

    @Override
    @Transactional
    public int carryForward(Integer fiscalYear, Integer fromMonth, Integer toMonth) {
        return mapper.carryForward(fiscalYear, fromMonth, toMonth);
    }

    @Override
    @Transactional
    public void updateWithOptimisticLock(MonthlyAccountBalance balance) {
        int updatedCount = mapper.updateWithOptimisticLock(balance);

        if (updatedCount == 0) {
            Integer currentVersion = mapper.findVersion(
                    balance.getFiscalYear(),
                    balance.getMonth(),
                    balance.getAccountCode(),
                    balance.getSubAccountCode(),
                    balance.getDepartmentCode(),
                    balance.getProjectCode(),
                    balance.getClosingJournalFlag());

            String key = String.format("%d/%d/%s/%s/%s/%s/%s",
                    balance.getFiscalYear(),
                    balance.getMonth(),
                    balance.getAccountCode(),
                    balance.getSubAccountCode(),
                    balance.getDepartmentCode(),
                    balance.getProjectCode(),
                    balance.getClosingJournalFlag());

            if (currentVersion == null) {
                throw new OptimisticLockException("月次残高", key);
            } else {
                throw new OptimisticLockException("月次残高", key,
                        balance.getVersion(), currentVersion);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
        mapper.deleteAll();
    }

    @Override
    public PageResult<TrialBalanceLine> findWithPagination(
            int page, int size,
            Integer fiscalYear, Integer month,
            String accountCode) {
        int offset = page * size;
        List<TrialBalanceLine> content = mapper.findWithPagination(
                offset, size, fiscalYear, month, accountCode);
        long totalElements = mapper.countWithCondition(fiscalYear, month, accountCode);
        return new PageResult<>(content, page, size, totalElements);
    }
}
