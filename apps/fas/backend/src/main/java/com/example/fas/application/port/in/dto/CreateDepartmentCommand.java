package com.example.fas.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門登録コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentCommand {
    private String departmentCode;
    private String departmentName;
    private String departmentShortName;
    private Integer organizationLevel;
    private String departmentPath;
    private Integer lowestLevelFlag;
}
