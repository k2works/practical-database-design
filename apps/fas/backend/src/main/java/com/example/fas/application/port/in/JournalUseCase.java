package com.example.fas.application.port.in;

import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.dto.JournalImportResult;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.domain.model.common.PageResult;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * 仕訳ユースケース（Input Port）.
 */
public interface JournalUseCase {

    /**
     * 仕訳を取得.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return 仕訳レスポンス
     */
    JournalResponse getJournal(String voucherNumber);

    /**
     * 期間指定で仕訳を検索.
     *
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 仕訳レスポンスリスト
     */
    List<JournalResponse> getJournalsByDateRange(LocalDate fromDate, LocalDate toDate);

    /**
     * 勘定科目コードで仕訳を検索.
     *
     * @param accountCode 勘定科目コード
     * @return 仕訳レスポンスリスト
     */
    List<JournalResponse> getJournalsByAccountCode(String accountCode);

    /**
     * 仕訳を登録.
     *
     * @param command 登録コマンド
     * @return 登録した仕訳
     */
    JournalResponse createJournal(CreateJournalCommand command);

    /**
     * 仕訳を取消（赤伝処理）.
     *
     * @param voucherNumber 取消対象の仕訳伝票番号
     * @return 赤伝票
     */
    JournalResponse cancelJournal(String voucherNumber);

    /**
     * 仕訳を削除.
     *
     * @param voucherNumber 仕訳伝票番号
     */
    void deleteJournal(String voucherNumber);

    /**
     * ページネーション付きで仕訳を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param fromDate 開始日（null 可）
     * @param toDate 終了日（null 可）
     * @param keyword キーワード（null 可）
     * @return ページネーション結果
     */
    PageResult<JournalResponse> getJournals(int page, int size, LocalDate fromDate, LocalDate toDate, String keyword);

    /**
     * CSV ファイルから仕訳を取り込む.
     *
     * @param inputStream CSV ファイルの入力ストリーム
     * @param skipHeaderLine ヘッダー行をスキップするか
     * @param skipEmptyLines 空行をスキップするか
     * @return 取込結果
     */
    JournalImportResult importJournalsFromCsv(InputStream inputStream, boolean skipHeaderLine, boolean skipEmptyLines);

    /**
     * 期間指定で仕訳件数を取得.
     *
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 仕訳件数
     */
    long countJournalsByDateRange(LocalDate fromDate, LocalDate toDate);
}
