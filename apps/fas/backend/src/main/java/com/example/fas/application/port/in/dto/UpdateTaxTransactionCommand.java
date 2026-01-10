package com.example.fas.application.port.in.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 課税取引更新コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaxTransactionCommand {
    private String taxName;
    private BigDecimal taxRate;
}
