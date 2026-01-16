package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueInstructionRepository;
import com.example.pms.domain.model.inventory.IssueInstruction;
import com.example.pms.infrastructure.out.persistence.mapper.IssueInstructionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 払出指示リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class IssueInstructionRepositoryImpl implements IssueInstructionRepository {

    private final IssueInstructionMapper issueInstructionMapper;

    @Override
    public void save(IssueInstruction instruction) {
        if (instruction.getId() == null) {
            issueInstructionMapper.insert(instruction);
        } else {
            issueInstructionMapper.update(instruction);
        }
    }

    @Override
    public Optional<IssueInstruction> findById(Integer id) {
        return Optional.ofNullable(issueInstructionMapper.findById(id));
    }

    @Override
    public Optional<IssueInstruction> findByInstructionNumber(String instructionNumber) {
        return Optional.ofNullable(issueInstructionMapper.findByInstructionNumber(instructionNumber));
    }

    @Override
    public List<IssueInstruction> findByOrderNumber(String orderNumber) {
        return issueInstructionMapper.findByOrderNumber(orderNumber);
    }

    @Override
    public List<IssueInstruction> findByLocationCode(String locationCode) {
        return issueInstructionMapper.findByLocationCode(locationCode);
    }

    @Override
    public List<IssueInstruction> findAll() {
        return issueInstructionMapper.findAll();
    }

    @Override
    public void deleteAll() {
        issueInstructionMapper.deleteAll();
    }
}
