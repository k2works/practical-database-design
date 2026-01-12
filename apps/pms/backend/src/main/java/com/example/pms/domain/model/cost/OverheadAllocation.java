package com.example.pms.domain.model.cost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 製造間接費配賦データエンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverheadAllocation {
    private Integer id;
    private String workOrderNumber;
    private String accountingPeriod;
    private String allocationBasis;
    private BigDecimal basisAmount;
    private BigDecimal allocationRate;
    private BigDecimal allocatedAmount;
    private LocalDateTime createdAt;
}
