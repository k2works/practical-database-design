package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 損益計算書を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeStatement {

    private Integer fiscalYear;           // 決算期
    private YearMonth fromMonth;          // 期間開始月
    private YearMonth toMonth;            // 期間終了月

    // 売上高
    private List<IncomeStatementLine> salesRevenue;
    private BigDecimal totalSalesRevenue;             // 売上高合計

    // 売上原価
    private List<IncomeStatementLine> costOfSales;
    private BigDecimal totalCostOfSales;              // 売上原価合計

    // 売上総利益
    private BigDecimal grossProfit;                   // 売上総利益

    // 販売費及び一般管理費
    private List<IncomeStatementLine> sellingGeneralExpenses;
    private BigDecimal totalSellingGeneralExpenses;   // 販管費合計

    // 営業利益
    private BigDecimal operatingIncome;               // 営業利益

    // 営業外収益
    private List<IncomeStatementLine> nonOperatingIncome;
    private BigDecimal totalNonOperatingIncome;       // 営業外収益合計

    // 営業外費用
    private List<IncomeStatementLine> nonOperatingExpenses;
    private BigDecimal totalNonOperatingExpenses;     // 営業外費用合計

    // 経常利益
    private BigDecimal ordinaryIncome;                // 経常利益

    // 特別利益
    private List<IncomeStatementLine> extraordinaryIncome;
    private BigDecimal totalExtraordinaryIncome;      // 特別利益合計

    // 特別損失
    private List<IncomeStatementLine> extraordinaryLoss;
    private BigDecimal totalExtraordinaryLoss;        // 特別損失合計

    // 税引前当期純利益
    private BigDecimal incomeBeforeTaxes;             // 税引前当期純利益

    // 法人税等
    private BigDecimal incomeTaxes;                   // 法人税等

    // 当期純利益
    private BigDecimal netIncome;                     // 当期純利益
}
