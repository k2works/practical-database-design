package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.CompletionResult;

import java.util.List;
import java.util.Optional;

/**
 * 完成実績リポジトリ.
 */
public interface CompletionResultRepository {

    void save(CompletionResult completionResult);

    Optional<CompletionResult> findById(Integer id);

    Optional<CompletionResult> findByCompletionResultNumber(String completionResultNumber);

    List<CompletionResult> findByWorkOrderNumber(String workOrderNumber);

    List<CompletionResult> findAll();

    /**
     * ページネーション付きで完成実績を取得する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード
     * @return 完成実績リスト
     */
    List<CompletionResult> findWithPagination(int offset, int limit, String keyword);

    /**
     * 完成実績の件数を取得する.
     *
     * @param keyword キーワード
     * @return 件数
     */
    long count(String keyword);

    /**
     * 完成実績を更新する.
     *
     * @param completionResult 完成実績
     */
    void update(CompletionResult completionResult);

    /**
     * 完成実績番号で削除する.
     *
     * @param completionResultNumber 完成実績番号
     */
    void deleteByCompletionResultNumber(String completionResultNumber);

    void deleteAll();
}
