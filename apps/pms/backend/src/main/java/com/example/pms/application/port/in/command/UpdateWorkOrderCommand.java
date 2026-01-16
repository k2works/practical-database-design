package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.process.WorkOrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作業指示更新コマンド.
 */
@Value
@Builder
public class UpdateWorkOrderCommand {
    String orderNumber;
    LocalDate workOrderDate;
    String itemCode;
    BigDecimal orderQuantity;
    String locationCode;
    LocalDate plannedStartDate;
    LocalDate plannedEndDate;
    LocalDate actualStartDate;
    LocalDate actualEndDate;
    BigDecimal completedQuantity;
    BigDecimal totalGoodQuantity;
    BigDecimal totalDefectQuantity;
    WorkOrderStatus status;
    Boolean completedFlag;
    String remarks;
    Integer version;
}
