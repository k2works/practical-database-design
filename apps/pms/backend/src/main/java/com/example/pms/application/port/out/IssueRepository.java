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

    void deleteAll();
}
