package com.example.sms.domain.model.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 社員エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String employeeCode;
    private String employeeName;
    private String employeeNameKana;
    private String departmentCode;
    private LocalDate departmentStartDate;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
