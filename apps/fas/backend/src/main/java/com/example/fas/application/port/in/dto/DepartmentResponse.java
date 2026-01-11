package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.department.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private String departmentCode;
    private String departmentName;
    private String departmentShortName;
    private Integer organizationLevel;
    private String departmentPath;
    private boolean lowestLevel;
    private String organizationLevelName;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param department 部門エンティティ
     * @return レスポンス DTO
     */
    public static DepartmentResponse from(Department department) {
        return DepartmentResponse.builder()
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .departmentShortName(department.getDepartmentShortName())
                .organizationLevel(department.getOrganizationLevel())
                .departmentPath(department.getDepartmentPath())
                .lowestLevel(department.isLowestLevel())
                .organizationLevelName(getOrganizationLevelName(department.getOrganizationLevel()))
                .build();
    }

    private static String getOrganizationLevelName(Integer level) {
        if (level == null) {
            return "";
        }
        return switch (level) {
            case 0 -> "全社";
            case 1 -> "本部";
            case 2 -> "部";
            case 3 -> "課";
            default -> "その他";
        };
    }
}
