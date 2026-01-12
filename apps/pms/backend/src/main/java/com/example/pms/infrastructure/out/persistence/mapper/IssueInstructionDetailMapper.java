package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.IssueInstructionDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 払出指示明細 Mapper.
 */
@Mapper
public interface IssueInstructionDetailMapper {

    void insert(IssueInstructionDetail detail);

    void update(IssueInstructionDetail detail);

    IssueInstructionDetail findById(Integer id);

    IssueInstructionDetail findByInstructionNumberAndLineNumber(
            @Param("instructionNumber") String instructionNumber,
            @Param("lineNumber") Integer lineNumber);

    List<IssueInstructionDetail> findByInstructionNumber(String instructionNumber);

    List<IssueInstructionDetail> findAll();

    void deleteAll();
}
