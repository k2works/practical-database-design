package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.IssueInstruction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 払出指示 Mapper.
 */
@Mapper
public interface IssueInstructionMapper {

    void insert(IssueInstruction instruction);

    void update(IssueInstruction instruction);

    IssueInstruction findById(Integer id);

    IssueInstruction findByInstructionNumber(String instructionNumber);

    List<IssueInstruction> findByOrderNumber(String orderNumber);

    List<IssueInstruction> findByLocationCode(String locationCode);

    List<IssueInstruction> findAll();

    void deleteAll();
}
