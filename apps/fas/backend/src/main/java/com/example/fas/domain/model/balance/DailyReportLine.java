package com.example.fas.domain.model.balance;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日計表の1行を表すDTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportLine {

    /** 借方を表す文字列定数. */
    private static final String DEBIT_TYPE = "借方";

    private LocalDate postingDate;           // 起票日
    private String accountCode;              // 勘定科目コード
    private String accountName;              // 勘定科目名
    private String bsplType;                 // BSPL区分
    private String debitCreditType;          // 貸借区分
    private BigDecimal debitTotal;           // 借方合計
    private BigDecimal creditTotal;          // 貸方合計
    private BigDecimal balance;              // 残高

    /**
     * 勘定科目の性質に基づいた残高を計算.
     * 借方科目（資産・費用）: 借方 - 貸方
     * 貸方科目（負債・資本・収益）: 貸方 - 借方
     *
     * @return 計算された残高
     */
    public BigDecimal calculateBalance() {
        if (DEBIT_TYPE.equals(debitCreditType)) {
            return debitTotal.subtract(creditTotal);
        } else {
            return creditTotal.subtract(debitTotal);
        }
    }
}
