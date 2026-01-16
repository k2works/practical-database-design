package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.command.RecordCompletionCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 完成実績登録リクエスト DTO.
 */
@Data
public class RecordCompletionRequest {

    @NotNull(message = "完成数量は必須です")
    @PositiveOrZero(message = "完成数量は0以上である必要があります")
    private BigDecimal completedQuantity;

    @NotNull(message = "良品数は必須です")
    @PositiveOrZero(message = "良品数は0以上である必要があります")
    private BigDecimal goodQuantity;

    @PositiveOrZero(message = "不良品数は0以上である必要があります")
    private BigDecimal defectQuantity;

    /**
     * コマンドに変換する.
     *
     * @return RecordCompletionCommand
     */
    public RecordCompletionCommand toCommand() {
        return new RecordCompletionCommand(
            completedQuantity,
            goodQuantity,
            defectQuantity != null ? defectQuantity : BigDecimal.ZERO
        );
    }
}
