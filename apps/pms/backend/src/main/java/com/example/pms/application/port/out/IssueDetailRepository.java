package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.IssueDetail;

import java.util.List;
import java.util.Optional;

/**
 * 払出明細リポジトリ.
 */
public interface IssueDetailRepository {

    void save(IssueDetail detail);

    Optional<IssueDetail> findById(Integer id);

    Optional<IssueDetail> findByIssueNumberAndLineNumber(String issueNumber, Integer lineNumber);

    List<IssueDetail> findByIssueNumber(String issueNumber);

    List<IssueDetail> findAll();

    void deleteAll();
}
