package com.example.pms.domain.model.defect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 欠点マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Defect {
    private String defectCode;
    private String defectName;
    private String defectCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
