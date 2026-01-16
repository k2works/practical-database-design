package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 払出指示明細データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInstructionDetail {
    private Integer id;
    private String instructionNumber;
    private Integer lineNumber;
    private String itemCode;
    private Integer routingSequence;
    private BigDecimal issueQuantity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
