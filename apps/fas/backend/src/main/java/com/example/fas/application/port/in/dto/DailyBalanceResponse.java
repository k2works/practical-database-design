package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.balance.DailyReportLine;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日次残高レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBalanceResponse {
    private LocalDate postingDate;
    private String accountCode;
    private String accountName;
    private String bsPlType;
    private String debitCreditType;
    private BigDecimal debitTotal;
    private BigDecimal creditTotal;
    private BigDecimal balance;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param line 日計表行
     * @return レスポンス DTO
     */
    public static DailyBalanceResponse from(DailyReportLine line) {
        return DailyBalanceResponse.builder()
                .postingDate(line.getPostingDate())
                .accountCode(line.getAccountCode())
                .accountName(line.getAccountName())
                .bsPlType(line.getBsplType())
                .debitCreditType(line.getDebitCreditType())
                .debitTotal(line.getDebitTotal())
                .creditTotal(line.getCreditTotal())
                .balance(line.getBalance())
                .build();
    }
}
