package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.report.GeneralLedgerEntry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 仕訳マッパー.
 */
@Mapper
public interface JournalMapper {

    void insertJournal(Journal journal);

    void insertJournalDetail(JournalDetail detail);

    void insertJournalDebitCreditDetail(JournalDebitCreditDetail dcDetail);

    Optional<Journal> findByVoucherNumber(@Param("voucherNumber") String voucherNumber);

    List<JournalDetail> findDetailsByVoucherNumber(@Param("voucherNumber") String voucherNumber);

    List<JournalDebitCreditDetail> findDCDetailsByVoucherAndLine(
            @Param("voucherNumber") String voucherNumber,
            @Param("lineNumber") Integer lineNumber);

    List<String> findVoucherNumbersByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    List<String> findVoucherNumbersByAccountCode(@Param("accountCode") String accountCode);

    List<String> findVoucherNumbersByDepartmentCode(@Param("departmentCode") String departmentCode);

    void deleteJournal(@Param("voucherNumber") String voucherNumber);

    void deleteAll();

    // 楽観ロック対応メソッド

    /**
     * 仕訳と明細を一括取得（JOIN）.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return 明細を含む仕訳
     */
    Optional<Journal> findWithDetailsByVoucherNumber(@Param("voucherNumber") String voucherNumber);

    /**
     * 楽観ロック用バージョン取得.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return バージョン
     */
    Integer findVersionByVoucherNumber(@Param("voucherNumber") String voucherNumber);

    /**
     * 仕訳ヘッダ更新（楽観ロック付き）.
     *
     * @param journal 仕訳
     * @return 更新件数
     */
    int updateJournalWithOptimisticLock(Journal journal);

    /**
     * 仕訳明細更新（楽観ロック付き）.
     *
     * @param detail 仕訳明細
     * @return 更新件数
     */
    int updateJournalDetailWithOptimisticLock(JournalDetail detail);

    /**
     * 仕訳貸借明細更新（楽観ロック付き）.
     *
     * @param dcDetail 仕訳貸借明細
     * @return 更新件数
     */
    int updateJournalDebitCreditDetailWithOptimisticLock(JournalDebitCreditDetail dcDetail);

    /**
     * ページネーション付きで仕訳を検索.
     *
     * @param offset オフセット
     * @param limit 件数
     * @param fromDate 開始日（null 可）
     * @param toDate 終了日（null 可）
     * @param keyword キーワード（null 可）
     * @return 仕訳リスト
     */
    List<Journal> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("keyword") String keyword);

    /**
     * 検索条件に一致する仕訳の件数を取得.
     *
     * @param fromDate 開始日（null 可）
     * @param toDate 終了日（null 可）
     * @param keyword キーワード（null 可）
     * @return 件数
     */
    long count(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("keyword") String keyword);

    /**
     * 総勘定元帳エントリを取得.
     *
     * @param accountCode 勘定科目コード
     * @param fromDate 開始日
     * @param toDate 終了日
     * @param offset オフセット
     * @param limit 件数
     * @return 総勘定元帳エントリリスト
     */
    List<GeneralLedgerEntry> findGeneralLedgerEntries(
            @Param("accountCode") String accountCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 総勘定元帳エントリ件数を取得.
     *
     * @param accountCode 勘定科目コード
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 件数
     */
    long countGeneralLedgerEntries(
            @Param("accountCode") String accountCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    /**
     * 指定日以前の残高を取得.
     *
     * @param accountCode 勘定科目コード
     * @param beforeDate 基準日
     * @return 残高
     */
    BigDecimal getOpeningBalance(
            @Param("accountCode") String accountCode,
            @Param("beforeDate") LocalDate beforeDate);

    /**
     * 期間指定で仕訳件数を取得.
     *
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 仕訳件数
     */
    long countByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
