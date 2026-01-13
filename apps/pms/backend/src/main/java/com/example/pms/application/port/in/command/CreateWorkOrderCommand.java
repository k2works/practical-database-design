package com.example.pms.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作業指示登録コマンド.
 */
@Value
@Builder
public class CreateWorkOrderCommand {
    String orderNumber;
    String itemCode;
    BigDecimal orderQuantity;
    String locationCode;
    LocalDate plannedStartDate;
    LocalDate plannedEndDate;
    String remarks;
}
