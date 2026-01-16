package com.example.pms.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * MRP 実行リクエスト DTO.
 */
@Data
public class ExecuteMrpRequest {

    @NotNull(message = "開始日は必須です")
    private LocalDate startDate;

    @NotNull(message = "終了日は必須です")
    private LocalDate endDate;
}
