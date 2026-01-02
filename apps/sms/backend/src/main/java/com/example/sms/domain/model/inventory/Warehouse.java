package com.example.sms.domain.model.inventory;

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
    private String warehouseName;
    private String warehouseNameKana;
    private WarehouseType warehouseType;
    private String postalCode;
    private String address;
    private String phoneNumber;
    private Boolean activeFlag;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 自社倉庫かどうか.
     */
    public boolean isOwnWarehouse() {
        return this.warehouseType == WarehouseType.OWN;
    }

    /**
     * 仮想倉庫かどうか.
     */
    public boolean isVirtualWarehouse() {
        return this.warehouseType == WarehouseType.VIRTUAL;
    }
}
