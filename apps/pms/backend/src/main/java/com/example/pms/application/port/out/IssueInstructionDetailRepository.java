package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.IssueInstructionDetail;

import java.util.List;
import java.util.Optional;

/**
 * 払出指示明細リポジトリ.
 */
public interface IssueInstructionDetailRepository {

    void save(IssueInstructionDetail detail);

    Optional<IssueInstructionDetail> findById(Integer id);

    Optional<IssueInstructionDetail> findByInstructionNumberAndLineNumber(String instructionNumber, Integer lineNumber);

    List<IssueInstructionDetail> findByInstructionNumber(String instructionNumber);

    List<IssueInstructionDetail> findAll();

    void deleteAll();
}
