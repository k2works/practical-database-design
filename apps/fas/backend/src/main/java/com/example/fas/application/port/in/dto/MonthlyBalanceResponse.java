package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 月次残高レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBalanceResponse {
    private Integer fiscalYear;
    private Integer month;
    private String accountCode;
    private String accountName;
    private String bsPlType;
    private String debitCreditType;
    private BigDecimal openingBalance;
    private BigDecimal debitTotal;
    private BigDecimal creditTotal;
    private BigDecimal closingBalance;

    /**
     * 試算表行からレスポンス DTO を生成.
     *
     * @param line 試算表行
     * @return レスポンス DTO
     */
    public static MonthlyBalanceResponse from(TrialBalanceLine line) {
        return MonthlyBalanceResponse.builder()
                .fiscalYear(line.getFiscalYear())
                .month(line.getMonth())
                .accountCode(line.getAccountCode())
                .accountName(line.getAccountName())
                .bsPlType(line.getBsplType())
                .debitCreditType(line.getDebitCreditType())
                .openingBalance(line.getOpeningBalance())
                .debitTotal(line.getDebitTotal())
                .creditTotal(line.getCreditTotal())
                .closingBalance(line.getClosingBalance())
                .build();
    }

    /**
     * 月次残高エンティティからレスポンス DTO を生成.
     *
     * @param balance 月次残高エンティティ
     * @return レスポンス DTO
     */
    public static MonthlyBalanceResponse from(MonthlyAccountBalance balance) {
        return MonthlyBalanceResponse.builder()
                .fiscalYear(balance.getFiscalYear())
                .month(balance.getMonth())
                .accountCode(balance.getAccountCode())
                .openingBalance(balance.getOpeningBalance())
                .debitTotal(balance.getDebitAmount())
                .creditTotal(balance.getCreditAmount())
                .closingBalance(balance.getClosingBalance())
                .build();
    }

    /**
     * 当月増減を取得.
     *
     * @return 当月増減
     */
    public BigDecimal getMonthlyChange() {
        return closingBalance.subtract(openingBalance);
    }
}
