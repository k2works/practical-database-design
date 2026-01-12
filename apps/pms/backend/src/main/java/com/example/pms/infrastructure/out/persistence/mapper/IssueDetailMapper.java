package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.IssueDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 払出明細 Mapper.
 */
@Mapper
public interface IssueDetailMapper {

    void insert(IssueDetail detail);

    void update(IssueDetail detail);

    IssueDetail findById(Integer id);

    IssueDetail findByIssueNumberAndLineNumber(
            @Param("issueNumber") String issueNumber,
            @Param("lineNumber") Integer lineNumber);

    List<IssueDetail> findByIssueNumber(String issueNumber);

    List<IssueDetail> findAll();

    void deleteAll();
}
