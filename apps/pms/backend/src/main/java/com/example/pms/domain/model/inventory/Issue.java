package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 払出データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    private Integer id;
    private String issueNumber;
    private String workOrderNumber;
    private Integer routingSequence;
    private String locationCode;
    private LocalDate issueDate;
    private String issuerCode;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private List<IssueDetail> details;
}
