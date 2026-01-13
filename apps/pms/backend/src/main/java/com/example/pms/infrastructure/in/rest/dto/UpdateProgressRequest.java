package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.process.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 進捗更新リクエスト DTO.
 */
@Data
public class UpdateProgressRequest {

    @NotNull(message = "ステータスは必須です")
    private WorkOrderStatus status;
}
