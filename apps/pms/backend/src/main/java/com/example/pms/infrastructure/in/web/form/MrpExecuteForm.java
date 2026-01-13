package com.example.pms.infrastructure.in.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * MRP 実行フォーム.
 */
@Data
public class MrpExecuteForm {

    @NotNull(message = "開始日は必須です")
    private LocalDate startDate;

    @NotNull(message = "終了日は必須です")
    private LocalDate endDate;
}
