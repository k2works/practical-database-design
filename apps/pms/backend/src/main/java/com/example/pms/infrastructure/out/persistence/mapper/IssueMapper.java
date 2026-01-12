package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.Issue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 払出 Mapper.
 */
@Mapper
public interface IssueMapper {

    void insert(Issue issue);

    void update(Issue issue);

    Issue findById(Integer id);

    Issue findByIssueNumber(String issueNumber);

    /**
     * 払出番号で検索（明細を含む）.
     */
    Issue findByIssueNumberWithDetails(String issueNumber);

    List<Issue> findByWorkOrderNumber(String workOrderNumber);

    List<Issue> findByLocationCode(String locationCode);

    List<Issue> findAll();

    void deleteAll();
}
