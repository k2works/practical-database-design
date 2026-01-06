package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.exception.OptimisticLockException;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.infrastructure.out.persistence.mapper.JournalMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仕訳リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class JournalRepositoryImpl implements JournalRepository {

    private final JournalMapper journalMapper;

    @Override
    @Transactional
    public void save(Journal journal) {
        journalMapper.insertJournal(journal);

        journal.getDetails().forEach(detail -> {
            journalMapper.insertJournalDetail(detail);

            detail.getDebitCreditDetails()
                .forEach(journalMapper::insertJournalDebitCreditDetail);
        });
    }

    @Override
    public Optional<Journal> findByVoucherNumber(String voucherNumber) {
        return journalMapper.findByVoucherNumber(voucherNumber)
            .map(journal -> {
                var details = journalMapper.findDetailsByVoucherNumber(voucherNumber);
                details.forEach(detail -> {
                    var dcDetails = journalMapper.findDCDetailsByVoucherAndLine(
                        voucherNumber, detail.getLineNumber());
                    detail.setDebitCreditDetails(dcDetails);
                });
                journal.setDetails(details);
                return journal;
            });
    }

    @Override
    public List<Journal> findByPostingDateBetween(LocalDate fromDate, LocalDate toDate) {
        return journalMapper.findVoucherNumbersByDateRange(fromDate, toDate).stream()
            .map(this::findByVoucherNumber)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Journal> findByAccountCode(String accountCode) {
        return journalMapper.findVoucherNumbersByAccountCode(accountCode).stream()
            .map(this::findByVoucherNumber)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Journal> findByDepartmentCode(String departmentCode) {
        return journalMapper.findVoucherNumbersByDepartmentCode(departmentCode).stream()
            .map(this::findByVoucherNumber)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String voucherNumber) {
        journalMapper.deleteJournal(voucherNumber);
    }

    @Override
    @Transactional
    public void deleteAll() {
        journalMapper.deleteAll();
    }

    @Override
    public Optional<Journal> findWithDetails(String voucherNumber) {
        return journalMapper.findWithDetailsByVoucherNumber(voucherNumber);
    }

    @Override
    @Transactional
    public void update(Journal journal) {
        String voucherNumber = journal.getJournalVoucherNumber();

        // 現在のバージョンを取得
        Integer currentVersion = journalMapper.findVersionByVoucherNumber(voucherNumber);
        if (currentVersion == null) {
            throw new OptimisticLockException("仕訳", voucherNumber);
        }

        // バージョンチェック
        if (!currentVersion.equals(journal.getVersion())) {
            throw new OptimisticLockException("仕訳", voucherNumber,
                    journal.getVersion(), currentVersion);
        }

        // ヘッダを更新
        int updatedCount = journalMapper.updateJournalWithOptimisticLock(journal);
        if (updatedCount == 0) {
            // 再度バージョン確認
            Integer latestVersion = journalMapper.findVersionByVoucherNumber(voucherNumber);
            if (latestVersion == null) {
                throw new OptimisticLockException("仕訳", voucherNumber);
            }
            throw new OptimisticLockException("仕訳", voucherNumber,
                    journal.getVersion(), latestVersion);
        }

        // 明細を更新
        journal.getDetails().forEach(detail -> {
            int detailUpdated = journalMapper.updateJournalDetailWithOptimisticLock(detail);
            if (detailUpdated == 0) {
                throw new OptimisticLockException("仕訳明細",
                        voucherNumber + "-" + detail.getLineNumber());
            }

            // 貸借明細を更新
            detail.getDebitCreditDetails().forEach(dcDetail -> {
                int dcUpdated = journalMapper.updateJournalDebitCreditDetailWithOptimisticLock(dcDetail);
                if (dcUpdated == 0) {
                    throw new OptimisticLockException("仕訳貸借明細",
                            voucherNumber + "-" + dcDetail.getLineNumber() + "-"
                                    + dcDetail.getDebitCreditType());
                }
            });
        });
    }
}
