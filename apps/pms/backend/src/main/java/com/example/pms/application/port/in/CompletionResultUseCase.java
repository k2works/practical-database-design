package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.CompletionResult;

import java.util.List;
import java.util.Optional;

/**
 * 完成実績ユースケース（Input Port）.
 */
public interface CompletionResultUseCase {

    /**
     * ページネーション付きで完成実績一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<CompletionResult> getCompletionResultList(int page, int size, String keyword);

    /**
     * 全完成実績を取得する.
     *
     * @return 完成実績リスト
     */
    List<CompletionResult> getAllCompletionResults();

    /**
     * 完成実績番号で完成実績を取得する.
     *
     * @param completionResultNumber 完成実績番号
     * @return 完成実績
     */
    Optional<CompletionResult> getCompletionResult(String completionResultNumber);

    /**
     * 作業指示番号で完成実績を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 完成実績リスト
     */
    List<CompletionResult> getCompletionResultsByWorkOrder(String workOrderNumber);

    /**
     * 完成実績を登録する.
     *
     * @param completionResult 完成実績
     * @return 登録した完成実績
     */
    CompletionResult createCompletionResult(CompletionResult completionResult);

    /**
     * 完成実績を更新する.
     *
     * @param completionResultNumber 完成実績番号
     * @param completionResult 完成実績
     * @return 更新した完成実績
     */
    CompletionResult updateCompletionResult(String completionResultNumber, CompletionResult completionResult);

    /**
     * 完成実績を削除する.
     *
     * @param completionResultNumber 完成実績番号
     */
    void deleteCompletionResult(String completionResultNumber);
}
