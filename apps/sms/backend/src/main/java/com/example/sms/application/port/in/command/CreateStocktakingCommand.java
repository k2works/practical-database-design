package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 棚卸登録コマンド.
 */
public record CreateStocktakingCommand(
    String warehouseCode,
    LocalDate stocktakingDate,
    String remarks,
    List<CreateStocktakingDetailCommand> details
) {
    /**
     * 棚卸明細登録コマンド.
     */
    public record CreateStocktakingDetailCommand(
        String productCode,
        String locationCode,
        String lotNumber,
        BigDecimal bookQuantity,
        BigDecimal actualQuantity,
        String differenceReason
    ) {
    }
}
