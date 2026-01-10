package com.example.fas.domain.model.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 損益計算書の1行を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeStatementLine {

    private String accountCode;           // 勘定科目コード
    private String accountName;           // 勘定科目名
    private String category;              // 区分（売上高、売上原価、販管費など）
    private int displayOrder;             // 表示順
    private int indentLevel;              // インデントレベル
    private BigDecimal amount;            // 金額
    private boolean isSummary;            // 小計/合計行フラグ

    /**
     * 小計行を生成.
     *
     * @param label ラベル
     * @param amount 金額
     * @param displayOrder 表示順
     * @return 小計行
     */
    public static IncomeStatementLine subtotal(String label, BigDecimal amount, int displayOrder) {
        return IncomeStatementLine.builder()
                .accountCode("")
                .accountName(label)
                .category("")
                .displayOrder(displayOrder)
                .indentLevel(0)
                .amount(amount)
                .isSummary(true)
                .build();
    }
}
