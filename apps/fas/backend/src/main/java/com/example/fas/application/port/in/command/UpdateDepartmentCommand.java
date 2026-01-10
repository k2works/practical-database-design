package com.example.fas.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門更新コマンド DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentCommand {
    private String departmentName;
    private String departmentShortName;
    private Integer organizationLevel;
    private String departmentPath;
    private Integer lowestLevelFlag;
}
