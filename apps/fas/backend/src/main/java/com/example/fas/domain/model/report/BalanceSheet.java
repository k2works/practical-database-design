package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 貸借対照表を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSheet {

    private LocalDate asOfDate;           // 基準日
    private Integer fiscalYear;           // 決算期

    // 資産の部
    private List<BalanceSheetLine> currentAssets;       // 流動資産
    private List<BalanceSheetLine> fixedAssets;         // 固定資産
    private BigDecimal totalCurrentAssets;              // 流動資産合計
    private BigDecimal totalFixedAssets;                // 固定資産合計
    private BigDecimal totalAssets;                     // 資産合計

    // 負債の部
    private List<BalanceSheetLine> currentLiabilities;  // 流動負債
    private List<BalanceSheetLine> fixedLiabilities;    // 固定負債
    private BigDecimal totalCurrentLiabilities;         // 流動負債合計
    private BigDecimal totalFixedLiabilities;           // 固定負債合計
    private BigDecimal totalLiabilities;                // 負債合計

    // 純資産の部
    private List<BalanceSheetLine> equity;              // 純資産
    private BigDecimal totalEquity;                     // 純資産合計

    // 合計
    private BigDecimal totalLiabilitiesAndEquity;       // 負債純資産合計

    /**
     * 貸借が一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isBalanced() {
        return totalAssets.compareTo(totalLiabilitiesAndEquity) == 0;
    }
}
