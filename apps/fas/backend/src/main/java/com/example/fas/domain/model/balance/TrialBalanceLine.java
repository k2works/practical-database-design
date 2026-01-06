package com.example.fas.domain.model.balance;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合計残高試算表の1行を表すDTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceLine {
    private Integer fiscalYear;              // 決算期
    private Integer month;                   // 月度
    private String accountCode;              // 勘定科目コード
    private String accountName;              // 勘定科目名
    private String bsplType;                 // BSPL区分
    private String debitCreditType;          // 貸借区分
    private BigDecimal openingBalance;       // 月初残高
    private BigDecimal debitTotal;           // 借方合計
    private BigDecimal creditTotal;          // 貸方合計
    private BigDecimal closingBalance;       // 月末残高

    /**
     * 累計残高を取得する.
     *
     * @return 累計残高
     */
    public BigDecimal getCumulativeBalance() {
        return closingBalance;
    }

    /**
     * 当月増減を取得する.
     *
     * @return 当月増減
     */
    public BigDecimal getMonthlyChange() {
        return closingBalance.subtract(openingBalance);
    }
}
