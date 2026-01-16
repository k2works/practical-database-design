package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工程表.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRoute {
    private String itemCode;
    private Integer sequence;
    private String processCode;
    private BigDecimal standardTime;
    private BigDecimal setupTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
