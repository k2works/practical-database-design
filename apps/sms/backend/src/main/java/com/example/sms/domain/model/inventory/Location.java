package com.example.sms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ロケーションマスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String locationCode;
    private String warehouseCode;
    private String shelfNumber;
    private String zone;
    private String aisle;
    private String rack;
    private String level;
    private String bay;
    private Boolean activeFlag;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * ロケーションの完全表記を取得.
     * 例: A-01-02-03-04
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (zone != null) {
            sb.append(zone).append('-');
        }
        if (aisle != null) {
            sb.append(aisle).append('-');
        }
        if (rack != null) {
            sb.append(rack).append('-');
        }
        if (level != null) {
            sb.append(level).append('-');
        }
        if (bay != null) {
            sb.append(bay);
        }
        return sb.toString().replaceAll("-$", "");
    }
}
