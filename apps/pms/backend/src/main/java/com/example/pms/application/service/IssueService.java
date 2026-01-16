package com.example.pms.application.service;

import com.example.pms.application.port.in.IssueUseCase;
import com.example.pms.application.port.out.IssueRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Issue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 払出履歴サービス（Application Service）.
 */
@Service
@Transactional(readOnly = true)
public class IssueService implements IssueUseCase {

    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public PageResult<Issue> getIssueList(int page, int size, String keyword) {
        int offset = page * size;
        List<Issue> content = issueRepository.findWithPagination(offset, size, keyword);
        long totalElements = issueRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Optional<Issue> getIssue(String issueNumber) {
        return issueRepository.findByIssueNumberWithDetails(issueNumber);
    }

    @Override
    public List<Issue> getIssuesByWorkOrder(String workOrderNumber) {
        return issueRepository.findByWorkOrderNumber(workOrderNumber);
    }
}
