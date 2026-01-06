package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.DailyAccountBalanceRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.balance.DailyAccountBalance;
import com.example.fas.domain.model.balance.DailyReportLine;
import com.example.fas.infrastructure.out.persistence.mapper.DailyAccountBalanceMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日次勘定科目残高リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class DailyAccountBalanceRepositoryImpl implements DailyAccountBalanceRepository {

    private final DailyAccountBalanceMapper mapper;

    @Override
    @Transactional
    public void upsert(DailyAccountBalance balance) {
        mapper.upsert(balance);
    }

    @Override
    public Optional<DailyAccountBalance> findByKey(DailyAccountBalance.CompositeKey key) {
        return Optional.ofNullable(mapper.findByKey(
                key.getPostingDate(),
                key.getAccountCode(),
                key.getSubAccountCode(),
                key.getDepartmentCode(),
                key.getProjectCode(),
                key.getClosingJournalFlag()));
    }

    @Override
    public List<DailyAccountBalance> findByPostingDate(LocalDate postingDate) {
        return mapper.findByPostingDate(postingDate);
    }

    @Override
    public List<DailyAccountBalance> findByAccountCodeAndDateRange(
            String accountCode, LocalDate fromDate, LocalDate toDate) {
        return mapper.findByAccountCodeAndDateRange(accountCode, fromDate, toDate);
    }

    @Override
    public List<DailyReportLine> getDailyReport(LocalDate postingDate) {
        return mapper.getDailyReport(postingDate);
    }

    @Override
    @Transactional
    public void updateWithOptimisticLock(DailyAccountBalance balance) {
        int updatedCount = mapper.updateWithOptimisticLock(balance);

        if (updatedCount == 0) {
            Integer currentVersion = mapper.findVersion(
                    balance.getPostingDate(),
                    balance.getAccountCode(),
                    balance.getSubAccountCode(),
                    balance.getDepartmentCode(),
                    balance.getProjectCode(),
                    balance.getClosingJournalFlag());

            String key = String.format("%s/%s/%s/%s/%s/%s",
                    balance.getPostingDate(),
                    balance.getAccountCode(),
                    balance.getSubAccountCode(),
                    balance.getDepartmentCode(),
                    balance.getProjectCode(),
                    balance.getClosingJournalFlag());

            if (currentVersion == null) {
                throw new OptimisticLockException("日次残高", key);
            } else {
                throw new OptimisticLockException("日次残高", key,
                        balance.getVersion(), currentVersion);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
        mapper.deleteAll();
    }
}
