package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.IssueInstruction;

import java.util.List;
import java.util.Optional;

/**
 * 払出指示リポジトリ.
 */
public interface IssueInstructionRepository {

    void save(IssueInstruction instruction);

    Optional<IssueInstruction> findById(Integer id);

    Optional<IssueInstruction> findByInstructionNumber(String instructionNumber);

    List<IssueInstruction> findByOrderNumber(String orderNumber);

    List<IssueInstruction> findByLocationCode(String locationCode);

    List<IssueInstruction> findAll();

    void deleteAll();
}
