package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 在庫登録コマンド.
 */
public record CreateInventoryCommand(
    String warehouseCode,
    String productCode,
    String locationCode,
    BigDecimal currentQuantity,
    BigDecimal allocatedQuantity,
    BigDecimal orderedQuantity,
    String lotNumber,
    String serialNumber,
    LocalDate expirationDate
) {
}
