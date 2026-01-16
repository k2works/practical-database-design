package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 賃率マスタエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WageRate {
    private Integer id;
    private String workerCategoryCode;
    private String workerCategoryName;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private BigDecimal hourlyRate;
    private Boolean isDirect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 楽観ロック用バージョン
    @Builder.Default
    private Integer version = 1;

    /**
     * 指定日時点で有効かチェック.
     */
    public boolean isValidAt(LocalDate targetDate) {
        if (targetDate.isBefore(effectiveStartDate)) {
            return false;
        }
        return effectiveEndDate == null || !targetDate.isAfter(effectiveEndDate);
    }
}
