package com.example.fas.application.port.in.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 課税取引登録コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaxTransactionCommand {
    private String taxCode;
    private String taxName;
    private BigDecimal taxRate;
}
