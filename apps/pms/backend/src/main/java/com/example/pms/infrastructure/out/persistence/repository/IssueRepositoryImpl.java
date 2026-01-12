package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueRepository;
import com.example.pms.domain.model.inventory.Issue;
import com.example.pms.infrastructure.out.persistence.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 払出リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepository {

    private final IssueMapper issueMapper;

    @Override
    public void save(Issue issue) {
        if (issue.getId() == null) {
            issueMapper.insert(issue);
        } else {
            issueMapper.update(issue);
        }
    }

    @Override
    public Optional<Issue> findById(Integer id) {
        return Optional.ofNullable(issueMapper.findById(id));
    }

    @Override
    public Optional<Issue> findByIssueNumber(String issueNumber) {
        return Optional.ofNullable(issueMapper.findByIssueNumber(issueNumber));
    }

    @Override
    public Optional<Issue> findByIssueNumberWithDetails(String issueNumber) {
        return Optional.ofNullable(issueMapper.findByIssueNumberWithDetails(issueNumber));
    }

    @Override
    public List<Issue> findByWorkOrderNumber(String workOrderNumber) {
        return issueMapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<Issue> findByLocationCode(String locationCode) {
        return issueMapper.findByLocationCode(locationCode);
    }

    @Override
    public List<Issue> findAll() {
        return issueMapper.findAll();
    }

    @Override
    public void deleteAll() {
        issueMapper.deleteAll();
    }
}
