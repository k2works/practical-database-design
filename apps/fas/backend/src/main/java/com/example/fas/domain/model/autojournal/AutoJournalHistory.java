package com.example.fas.domain.model.autojournal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自動仕訳処理履歴エンティティ.
 * 自動仕訳処理の実行履歴を管理する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalHistory {
    private String processNumber;
    private LocalDateTime processDateTime;
    private LocalDate targetFromDate;
    private LocalDate targetToDate;
    private Integer totalCount;
    private Integer successCount;
    private Integer errorCount;
    private BigDecimal totalAmount;
    private String processedBy;
    private String remarks;
    private LocalDateTime createdAt;

    @Builder.Default
    private Integer version = 1;
}
