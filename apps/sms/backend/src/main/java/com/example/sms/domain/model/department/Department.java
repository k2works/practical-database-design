package com.example.sms.domain.model.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 部門エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class Department {
    private String departmentCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String departmentName;
    @Builder.Default
    private Integer hierarchyLevel = 0;
    private String departmentPath;
    @Builder.Default
    private boolean isLeaf = false;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
