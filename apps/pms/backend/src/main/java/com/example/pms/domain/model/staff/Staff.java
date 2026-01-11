package com.example.pms.domain.model.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 担当者マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private String staffCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String staffName;
    private String departmentCode;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
