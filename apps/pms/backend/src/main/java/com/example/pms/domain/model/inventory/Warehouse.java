package com.example.pms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 倉庫マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    private String warehouseCode;
    private String warehouseType;
    private String warehouseName;
    private String departmentCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
