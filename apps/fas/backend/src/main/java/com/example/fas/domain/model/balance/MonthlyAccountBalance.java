package com.example.fas.domain.model.balance;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 月次勘定科目残高エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAccountBalance {
    private Integer fiscalYear;              // 決算期
    private Integer month;                   // 月度
    private String accountCode;              // 勘定科目コード
    private String subAccountCode;           // 補助科目コード
    private String departmentCode;           // 部門コード
    private String projectCode;              // プロジェクトコード
    private Boolean closingJournalFlag;      // 決算仕訳フラグ
    private BigDecimal openingBalance;       // 月初残高
    private BigDecimal debitAmount;          // 借方金額
    private BigDecimal creditAmount;         // 貸方金額
    private BigDecimal closingBalance;       // 月末残高
    private LocalDateTime createdAt;         // 作成日時
    private LocalDateTime updatedAt;         // 更新日時

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    /**
     * 複合主キークラス.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompositeKey {
        private Integer fiscalYear;
        private Integer month;
        private String accountCode;
        private String subAccountCode;
        private String departmentCode;
        private String projectCode;
        private Boolean closingJournalFlag;
    }

    /**
     * 月末残高を再計算する.
     * 借方科目の場合: 月初残高 + 借方金額 - 貸方金額
     * 貸方科目の場合: 月初残高 - 借方金額 + 貸方金額
     *
     * @param accountType 勘定科目の貸借区分
     * @return 再計算した月末残高
     */
    public BigDecimal recalculateClosingBalance(DebitCreditType accountType) {
        if (accountType == DebitCreditType.DEBIT) {
            return openingBalance.add(debitAmount).subtract(creditAmount);
        } else {
            return openingBalance.subtract(debitAmount).add(creditAmount);
        }
    }

    /**
     * 当月の増減額を取得.
     *
     * @return 増減額（月末残高 - 月初残高）
     */
    public BigDecimal getNetChange() {
        return closingBalance.subtract(openingBalance);
    }
}
