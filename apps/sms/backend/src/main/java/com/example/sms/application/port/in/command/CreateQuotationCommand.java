package com.example.sms.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 見積登録コマンド.
 */
public record CreateQuotationCommand(
    LocalDate quotationDate,
    LocalDate validUntil,
    String customerCode,
    String customerBranchNumber,
    String salesRepCode,
    String subject,
    String remarks,
    List<CreateQuotationDetailCommand> details
) {
    /**
     * 見積明細登録コマンド.
     */
    public record CreateQuotationDetailCommand(
        String productCode,
        String productName,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice,
        String remarks
    ) {
    }
}
