package com.example.fas.application.port.out;

import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.journal.Journal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 仕訳リポジトリ（Output Port）.
 */
public interface JournalRepository {

    void save(Journal journal);

    Optional<Journal> findByVoucherNumber(String voucherNumber);

    List<Journal> findByPostingDateBetween(LocalDate fromDate, LocalDate toDate);

    List<Journal> findByAccountCode(String accountCode);

    List<Journal> findByDepartmentCode(String departmentCode);

    void delete(String voucherNumber);

    void deleteAll();

    /**
     * 仕訳と明細を一括取得（JOIN）.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return 明細を含む仕訳
     */
    Optional<Journal> findWithDetails(String voucherNumber);

    /**
     * 仕訳更新（楽観ロック付き）.
     *
     * @param journal 仕訳
     * @throws com.example.fas.domain.exception.OptimisticLockException バージョン不一致時
     */
    void update(Journal journal);

    /**
     * ページネーション付きで仕訳を検索.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param fromDate 開始日（null 可）
     * @param toDate 終了日（null 可）
     * @param keyword キーワード（null 可）
     * @return ページネーション結果
     */
    PageResult<Journal> findWithPagination(int page, int size, LocalDate fromDate, LocalDate toDate, String keyword);
}
