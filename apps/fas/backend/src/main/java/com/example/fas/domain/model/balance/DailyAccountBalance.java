package com.example.fas.domain.model.balance;

import com.example.fas.domain.model.account.DebitCreditType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日次勘定科目残高エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAccountBalance {
    private LocalDate postingDate;           // 起票日
    private String accountCode;              // 勘定科目コード
    private String subAccountCode;           // 補助科目コード
    private String departmentCode;           // 部門コード
    private String projectCode;              // プロジェクトコード
    private Boolean closingJournalFlag;      // 決算仕訳フラグ
    private BigDecimal debitAmount;          // 借方金額
    private BigDecimal creditAmount;         // 貸方金額
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
        private LocalDate postingDate;
        private String accountCode;
        private String subAccountCode;
        private String departmentCode;
        private String projectCode;
        private Boolean closingJournalFlag;
    }

    /**
     * 残高を計算する（借方 - 貸方）.
     * 資産・費用科目の場合は正の値が残高増加を意味する
     *
     * @return 残高
     */
    public BigDecimal getBalance() {
        return debitAmount.subtract(creditAmount);
    }

    /**
     * 指定した貸借区分での残高を取得.
     *
     * @param debitCreditType 貸借区分
     * @return 残高
     */
    public BigDecimal getBalanceByType(DebitCreditType debitCreditType) {
        if (debitCreditType == DebitCreditType.DEBIT) {
            return debitAmount.subtract(creditAmount);
        } else {
            return creditAmount.subtract(debitAmount);
        }
    }
}
