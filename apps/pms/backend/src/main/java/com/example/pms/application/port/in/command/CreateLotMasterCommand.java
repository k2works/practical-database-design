package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.quality.LotType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ロットマスタ登録コマンド.
 */
@Value
@Builder
public class CreateLotMasterCommand {
    String lotNumber;
    String itemCode;
    LotType lotType;
    LocalDate manufactureDate;
    LocalDate expirationDate;
    BigDecimal quantity;
    String warehouseCode;
    String remarks;
}
