package com.example.fas.domain.model.tax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 課税取引エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxTransaction {
    private String taxCode;
    private String taxName;
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("0.10");
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 税込金額を計算する.
     *
     * @param amount 税抜金額
     * @return 税込金額
     */
    public BigDecimal calculateTaxIncludedAmount(BigDecimal amount) {
        return amount.add(amount.multiply(taxRate));
    }

    /**
     * 消費税額を計算する.
     *
     * @param amount 税抜金額
     * @return 消費税額
     */
    public BigDecimal calculateTaxAmount(BigDecimal amount) {
        return amount.multiply(taxRate);
    }
}
