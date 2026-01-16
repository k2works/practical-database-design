package com.example.pms.domain.model.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工程マスタ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Process {
    private String processCode;
    private String processName;
    private String processType;
    private String locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
