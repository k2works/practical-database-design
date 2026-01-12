package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueInstructionDetailRepository;
import com.example.pms.domain.model.inventory.IssueInstructionDetail;
import com.example.pms.infrastructure.out.persistence.mapper.IssueInstructionDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 払出指示明細リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class IssueInstructionDetailRepositoryImpl implements IssueInstructionDetailRepository {

    private final IssueInstructionDetailMapper issueInstructionDetailMapper;

    @Override
    public void save(IssueInstructionDetail detail) {
        if (detail.getId() == null) {
            issueInstructionDetailMapper.insert(detail);
        } else {
            issueInstructionDetailMapper.update(detail);
        }
    }

    @Override
    public Optional<IssueInstructionDetail> findById(Integer id) {
        return Optional.ofNullable(issueInstructionDetailMapper.findById(id));
    }

    @Override
    public Optional<IssueInstructionDetail> findByInstructionNumberAndLineNumber(
            String instructionNumber, Integer lineNumber) {
        return Optional.ofNullable(
                issueInstructionDetailMapper.findByInstructionNumberAndLineNumber(instructionNumber, lineNumber));
    }

    @Override
    public List<IssueInstructionDetail> findByInstructionNumber(String instructionNumber) {
        return issueInstructionDetailMapper.findByInstructionNumber(instructionNumber);
    }

    @Override
    public List<IssueInstructionDetail> findAll() {
        return issueInstructionDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        issueInstructionDetailMapper.deleteAll();
    }
}
