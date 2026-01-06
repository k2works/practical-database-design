package com.example.fas.domain.model.journal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳明細エンティティ.
 * 仕訳の行単位の情報を管理する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalDetail {
    private String journalVoucherNumber;
    private Integer lineNumber;
    private String lineSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<JournalDebitCreditDetail> debitCreditDetails = new ArrayList<>();
}
