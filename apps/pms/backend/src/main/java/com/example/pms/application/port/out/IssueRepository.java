package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Issue;

import java.util.List;
import java.util.Optional;

/**
 * 払出リポジトリ.
 */
public interface IssueRepository {

    void save(Issue issue);

    Optional<Issue> findById(Integer id);

    Optional<Issue> findByIssueNumber(String issueNumber);

    /**
     * 払出番号で検索（明細を含む）.
     *
     * @param issueNumber 払出番号
     * @return 明細を含む払出データ
     */
    Optional<Issue> findByIssueNumberWithDetails(String issueNumber);

    List<Issue> findByWorkOrderNumber(String workOrderNumber);

    List<Issue> findByLocationCode(String locationCode);

    List<Issue> findAll();

    /**
     * ページネーション対応の払出履歴一覧取得.
     *
     * @param offset  オフセット
     * @param limit   取得件数
     * @param keyword 検索キーワード
     * @return 払出リスト
     */
    List<Issue> findWithPagination(int offset, int limit, String keyword);

    /**
     * 払出履歴件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);

    void deleteAll();
}
