package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 作業指示登録リクエスト DTO.
 */
@Data
public class CreateWorkOrderRequest {

    private String orderNumber;

    @NotBlank(message = "品目コードは必須です")
    private String itemCode;

    @NotNull(message = "指示数量は必須です")
    @Positive(message = "指示数量は正の値である必要があります")
    private BigDecimal orderQuantity;

    private String locationCode;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private String remarks;

    /**
     * コマンドに変換する.
     *
     * @return CreateWorkOrderCommand
     */
    public CreateWorkOrderCommand toCommand() {
        return new CreateWorkOrderCommand(
            orderNumber,
            itemCode,
            orderQuantity,
            locationCode,
            plannedStartDate,
            plannedEndDate,
            remarks
        );
    }
}
