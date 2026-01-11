package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 貸借対照表の1行を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSheetLine {

    private String accountCode;           // 勘定科目コード
    private String accountName;           // 勘定科目名
    private String category;              // 区分（流動資産、固定資産、流動負債など）
    private int displayOrder;             // 表示順
    private int indentLevel;              // インデントレベル
    private BigDecimal amount;            // 金額
    private boolean isSummary;            // 小計/合計行フラグ

    /**
     * 小計行を生成.
     *
     * @param category 区分名
     * @param amount 金額
     * @param displayOrder 表示順
     * @return 小計行
     */
    public static BalanceSheetLine subtotal(String category, BigDecimal amount, int displayOrder) {
        return BalanceSheetLine.builder()
                .accountCode("")
                .accountName(category + " 合計")
                .category(category)
                .displayOrder(displayOrder)
                .indentLevel(0)
                .amount(amount)
                .isSummary(true)
                .build();
    }
}
