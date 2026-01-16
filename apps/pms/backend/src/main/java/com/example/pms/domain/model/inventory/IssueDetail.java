package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 払出明細データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDetail {
    private Integer id;
    private String issueNumber;
    private Integer lineNumber;
    private String itemCode;
    private BigDecimal issueQuantity;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
