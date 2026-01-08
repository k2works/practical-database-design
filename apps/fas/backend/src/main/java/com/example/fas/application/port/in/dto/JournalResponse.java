package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.journal.Journal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalResponse {
    private String journalVoucherNumber;
    private LocalDate postingDate;
    private LocalDate entryDate;
    private String voucherType;
    private Boolean closingJournalFlag;
    private Boolean singleEntryFlag;
    private Boolean periodicPostingFlag;
    private String employeeCode;
    private String departmentCode;
    private Boolean redSlipFlag;
    private BigDecimal debitTotal;
    private BigDecimal creditTotal;
    private List<JournalDetailResponse> details;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param journal 仕訳ドメインモデル
     * @return レスポンス DTO
     */
    public static JournalResponse from(Journal journal) {
        return JournalResponse.builder()
                .journalVoucherNumber(journal.getJournalVoucherNumber())
                .postingDate(journal.getPostingDate())
                .entryDate(journal.getEntryDate())
                .voucherType(journal.getVoucherType() != null
                        ? journal.getVoucherType().name() : null)
                .closingJournalFlag(journal.getClosingJournalFlag())
                .singleEntryFlag(journal.getSingleEntryFlag())
                .periodicPostingFlag(journal.getPeriodicPostingFlag())
                .employeeCode(journal.getEmployeeCode())
                .departmentCode(journal.getDepartmentCode())
                .redSlipFlag(journal.getRedSlipFlag())
                .debitTotal(journal.getDebitTotal())
                .creditTotal(journal.getCreditTotal())
                .details(journal.getDetails().stream()
                        .map(JournalDetailResponse::from)
                        .toList())
                .build();
    }
}
