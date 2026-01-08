package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.balance.MonthlyAccountBalance;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/**
 * 月次残高レスポンス DTO.
 */
@Data
@Builder
public class MonthlyBalanceResponse {
    private Integer fiscalYear;
    private Integer month;
    private String accountCode;
    private String subAccountCode;
    private String departmentCode;
    private BigDecimal openingBalance;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private BigDecimal closingBalance;
    private BigDecimal netChange;

    /**
     * ドメインモデルからレスポンスを生成.
     *
     * @param balance 月次残高
     * @return レスポンス
     */
    public static MonthlyBalanceResponse from(MonthlyAccountBalance balance) {
        return MonthlyBalanceResponse.builder()
                .fiscalYear(balance.getFiscalYear())
                .month(balance.getMonth())
                .accountCode(balance.getAccountCode())
                .subAccountCode(balance.getSubAccountCode())
                .departmentCode(balance.getDepartmentCode())
                .openingBalance(balance.getOpeningBalance())
                .debitAmount(balance.getDebitAmount())
                .creditAmount(balance.getCreditAmount())
                .closingBalance(balance.getClosingBalance())
                .netChange(balance.getNetChange())
                .build();
    }
}
