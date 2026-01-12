package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工数実績データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaborHours {
    private Integer id;
    private String laborHoursNumber;
    private String workOrderNumber;
    private String itemCode;
    private Integer sequence;
    private String processCode;
    private String departmentCode;
    private String employeeCode;
    private LocalDate workDate;
    private BigDecimal hours;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private WorkOrder workOrder;
    private Process process;
}
