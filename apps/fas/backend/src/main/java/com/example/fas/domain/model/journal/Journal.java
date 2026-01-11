package com.example.fas.domain.model.journal;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳エンティティ.
 * 仕訳伝票のヘッダ情報を管理する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    private String journalVoucherNumber;
    private LocalDate postingDate;
    private LocalDate entryDate;
    private Boolean closingJournalFlag;
    private Boolean singleEntryFlag;
    private JournalVoucherType voucherType;
    private Boolean periodicPostingFlag;
    private String employeeCode;
    private String departmentCode;
    private Boolean redSlipFlag;
    private Integer redBlackVoucherNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private List<JournalDetail> details = new ArrayList<>();

    /**
     * 貸借バランスチェック.
     *
     * @return 借方合計と貸方合計が一致する場合 true
     */
    public boolean isBalanced() {
        return getDebitTotal().compareTo(getCreditTotal()) == 0;
    }

    /**
     * 借方合計を取得.
     *
     * @return 借方合計金額
     */
    public BigDecimal getDebitTotal() {
        return details.stream()
            .flatMap(d -> d.getDebitCreditDetails().stream())
            .filter(dc -> dc.getDebitCreditType() == DebitCreditType.DEBIT)
            .map(JournalDebitCreditDetail::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 貸方合計を取得.
     *
     * @return 貸方合計金額
     */
    public BigDecimal getCreditTotal() {
        return details.stream()
            .flatMap(d -> d.getDebitCreditDetails().stream())
            .filter(dc -> dc.getDebitCreditType() == DebitCreditType.CREDIT)
            .map(JournalDebitCreditDetail::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
