package com.example.pms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作業指示登録コマンド.
 */
public record CreateWorkOrderCommand(
    String orderNumber,
    String itemCode,
    BigDecimal orderQuantity,
    String locationCode,
    LocalDate plannedStartDate,
    LocalDate plannedEndDate,
    String remarks
) {
}
