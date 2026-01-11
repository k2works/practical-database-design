package com.example.fas.domain.model.report;

import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合計残高試算表を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TrialBalance {

    private static final String DEBIT_TYPE = "借方";

    private Integer fiscalYear;                    // 決算期
    private Integer month;                         // 月度
    private List<TrialBalanceLine> lines;          // 明細行
    private BigDecimal totalOpeningDebit;          // 月初借方合計
    private BigDecimal totalOpeningCredit;         // 月初貸方合計
    private BigDecimal totalDebit;                 // 借方合計
    private BigDecimal totalCredit;                // 貸方合計
    private BigDecimal totalClosingDebit;          // 月末借方合計
    private BigDecimal totalClosingCredit;         // 月末貸方合計

    /**
     * 明細行から合計を計算してインスタンスを生成.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @param lines 明細行リスト
     * @return 合計残高試算表インスタンス
     */
    @SuppressWarnings({"PMD.ShortMethodName", "PMD.CognitiveComplexity"})
    public static TrialBalance of(Integer fiscalYear, Integer month, List<TrialBalanceLine> lines) {
        BigDecimal totalOpeningDebit = BigDecimal.ZERO;
        BigDecimal totalOpeningCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalClosingDebit = BigDecimal.ZERO;
        BigDecimal totalClosingCredit = BigDecimal.ZERO;

        for (TrialBalanceLine line : lines) {
            // 月初残高
            if (line.getOpeningBalance() != null) {
                if (DEBIT_TYPE.equals(line.getDebitCreditType())) {
                    totalOpeningDebit = totalOpeningDebit.add(line.getOpeningBalance());
                } else {
                    totalOpeningCredit = totalOpeningCredit.add(line.getOpeningBalance());
                }
            }

            // 借方・貸方合計
            if (line.getDebitTotal() != null) {
                totalDebit = totalDebit.add(line.getDebitTotal());
            }
            if (line.getCreditTotal() != null) {
                totalCredit = totalCredit.add(line.getCreditTotal());
            }

            // 月末残高
            if (line.getClosingBalance() != null) {
                if (DEBIT_TYPE.equals(line.getDebitCreditType())) {
                    totalClosingDebit = totalClosingDebit.add(line.getClosingBalance());
                } else {
                    totalClosingCredit = totalClosingCredit.add(line.getClosingBalance());
                }
            }
        }

        return TrialBalance.builder()
                .fiscalYear(fiscalYear)
                .month(month)
                .lines(lines)
                .totalOpeningDebit(totalOpeningDebit)
                .totalOpeningCredit(totalOpeningCredit)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .totalClosingDebit(totalClosingDebit)
                .totalClosingCredit(totalClosingCredit)
                .build();
    }

    /**
     * 月初残高が貸借一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isOpeningBalanced() {
        return totalOpeningDebit.compareTo(totalOpeningCredit) == 0;
    }

    /**
     * 借方・貸方合計が一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isTransactionBalanced() {
        return totalDebit.compareTo(totalCredit) == 0;
    }

    /**
     * 月末残高が貸借一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isClosingBalanced() {
        return totalClosingDebit.compareTo(totalClosingCredit) == 0;
    }

    /**
     * 全体が貸借一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isBalanced() {
        return isOpeningBalanced() && isTransactionBalanced() && isClosingBalanced();
    }
}
