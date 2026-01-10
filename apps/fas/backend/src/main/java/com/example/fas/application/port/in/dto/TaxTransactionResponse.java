package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.tax.TaxTransaction;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 課税取引レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxTransactionResponse {
    private String taxCode;
    private String taxName;
    private BigDecimal taxRate;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param taxTransaction 課税取引エンティティ
     * @return レスポンス DTO
     */
    public static TaxTransactionResponse from(TaxTransaction taxTransaction) {
        return TaxTransactionResponse.builder()
                .taxCode(taxTransaction.getTaxCode())
                .taxName(taxTransaction.getTaxName())
                .taxRate(taxTransaction.getTaxRate())
                .build();
    }
}
