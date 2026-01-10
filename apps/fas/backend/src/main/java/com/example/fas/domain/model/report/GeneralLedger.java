package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 総勘定元帳を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedger {

    private String accountCode;           // 勘定科目コード
    private String accountName;           // 勘定科目名
    private String bsplType;              // BSPL区分
    private String debitCreditType;       // 貸借区分
    private LocalDate fromDate;           // 期間開始日
    private LocalDate toDate;             // 期間終了日
    private BigDecimal openingBalance;    // 期首残高
    private BigDecimal closingBalance;    // 期末残高
    private BigDecimal totalDebit;        // 借方合計
    private BigDecimal totalCredit;       // 貸方合計
    private List<GeneralLedgerEntry> entries;  // 明細

    /**
     * 明細行から合計を計算してインスタンスを生成.
     *
     * @param accountCode 勘定科目コード
     * @param accountName 勘定科目名
     * @param bsplType BSPL区分
     * @param debitCreditType 貸借区分
     * @param fromDate 期間開始日
     * @param toDate 期間終了日
     * @param openingBalance 期首残高
     * @param entries 明細行リスト
     * @return 総勘定元帳インスタンス
     */
    @SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.ShortMethodName", "PMD.UseObjectForClearerAPI"})
    public static GeneralLedger of(
            String accountCode,
            String accountName,
            String bsplType,
            String debitCreditType,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal openingBalance,
            List<GeneralLedgerEntry> entries) {

        BigDecimal totalDebit = entries.stream()
                .map(e -> e.getDebitAmount() != null ? e.getDebitAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = entries.stream()
                .map(e -> e.getCreditAmount() != null ? e.getCreditAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closingBalance = openingBalance.add(totalDebit).subtract(totalCredit);

        return GeneralLedger.builder()
                .accountCode(accountCode)
                .accountName(accountName)
                .bsplType(bsplType)
                .debitCreditType(debitCreditType)
                .fromDate(fromDate)
                .toDate(toDate)
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .entries(entries)
                .build();
    }
}
