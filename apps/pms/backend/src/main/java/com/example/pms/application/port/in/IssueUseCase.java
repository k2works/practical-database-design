package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Issue;

import java.util.List;
import java.util.Optional;

/**
 * 払出履歴ユースケース（Input Port）.
 */
public interface IssueUseCase {

    /**
     * 払出履歴一覧を取得する（ページネーション対応）.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword 検索キーワード（払出番号、作業指示番号、場所コードで検索）
     * @return 払出のページ結果
     */
    PageResult<Issue> getIssueList(int page, int size, String keyword);

    /**
     * すべての払出履歴を取得する.
     *
     * @return 払出リスト
     */
    List<Issue> getAllIssues();

    /**
     * 払出を取得する（明細含む）.
     *
     * @param issueNumber 払出番号
     * @return 払出情報
     */
    Optional<Issue> getIssue(String issueNumber);

    /**
     * 作業指示番号で払出履歴を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 払出リスト
     */
    List<Issue> getIssuesByWorkOrder(String workOrderNumber);
}
