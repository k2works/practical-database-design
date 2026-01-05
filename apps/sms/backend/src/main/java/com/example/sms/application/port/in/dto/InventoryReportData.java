package com.example.sms.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 在庫レポート用データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportData {
    private String warehouseCode;
    private String warehouseName;
    private String productCode;
    private String productName;
    private BigDecimal currentStock;
    private BigDecimal allocatedStock;
    private BigDecimal availableStock;
    private String locationCode;
}
