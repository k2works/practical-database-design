package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 払出指示データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInstruction {
    private Integer id;
    private String instructionNumber;
    private String orderNumber;
    private LocalDate instructionDate;
    private String locationCode;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private List<IssueInstructionDetail> details;
}
