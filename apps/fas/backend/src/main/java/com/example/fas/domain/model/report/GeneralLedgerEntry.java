package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 総勘定元帳の1エントリを表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerEntry {

    private LocalDate postingDate;        // 起票日
    private String journalNumber;         // 仕訳番号
    private String description;           // 摘要
    private String counterAccountCode;    // 相手勘定科目コード
    private String counterAccountName;    // 相手勘定科目名
    private BigDecimal debitAmount;       // 借方金額
    private BigDecimal creditAmount;      // 貸方金額
    private BigDecimal balance;           // 残高

    /**
     * 借方エントリかどうかを判定.
     *
     * @return 借方の場合true
     */
    public boolean isDebit() {
        return debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 貸方エントリかどうかを判定.
     *
     * @return 貸方の場合true
     */
    public boolean isCredit() {
        return creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
