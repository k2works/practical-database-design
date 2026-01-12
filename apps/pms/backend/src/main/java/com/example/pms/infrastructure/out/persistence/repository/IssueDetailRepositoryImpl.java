package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueDetailRepository;
import com.example.pms.domain.model.inventory.IssueDetail;
import com.example.pms.infrastructure.out.persistence.mapper.IssueDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 払出明細リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class IssueDetailRepositoryImpl implements IssueDetailRepository {

    private final IssueDetailMapper issueDetailMapper;

    @Override
    public void save(IssueDetail detail) {
        if (detail.getId() == null) {
            issueDetailMapper.insert(detail);
        } else {
            issueDetailMapper.update(detail);
        }
    }

    @Override
    public Optional<IssueDetail> findById(Integer id) {
        return Optional.ofNullable(issueDetailMapper.findById(id));
    }

    @Override
    public Optional<IssueDetail> findByIssueNumberAndLineNumber(String issueNumber, Integer lineNumber) {
        return Optional.ofNullable(issueDetailMapper.findByIssueNumberAndLineNumber(issueNumber, lineNumber));
    }

    @Override
    public List<IssueDetail> findByIssueNumber(String issueNumber) {
        return issueDetailMapper.findByIssueNumber(issueNumber);
    }

    @Override
    public List<IssueDetail> findAll() {
        return issueDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        issueDetailMapper.deleteAll();
    }
}
