package com.example.fas.domain.model.autojournal;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自動仕訳データエンティティ.
 * 売上データから生成された仕訳データ（転記前の中間データ）.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalEntry {
    private String autoJournalNumber;
    private String salesNumber;
    private Integer salesLineNumber;
    private String patternCode;
    private LocalDate postingDate;
    private DebitCreditType debitCreditType;
    private String accountCode;
    private String subAccountCode;
    private String departmentCode;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private AutoJournalStatus status;
    private Boolean postedFlag;
    private LocalDate postedDate;
    private String journalVoucherNumber;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer version = 1;

    /** パターン情報（リレーション）. */
    private AutoJournalPattern pattern;
}
