package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.balance.TrialBalanceLine;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合計残高試算表レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceResponse {
    private Integer fiscalYear;
    private Integer month;
    private List<TrialBalanceLineResponse> lines;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;

    /**
     * 試算表行レスポンス.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrialBalanceLineResponse {
        private String accountCode;
        private String accountName;
        private String bsPlType;
        private String dcType;
        private BigDecimal openingBalance;
        private BigDecimal debitTotal;
        private BigDecimal creditTotal;
        private BigDecimal closingBalance;
        private BigDecimal monthlyChange;

        /**
         * ドメインモデルからレスポンスを生成.
         *
         * @param line 試算表行
         * @return レスポンス
         */
        public static TrialBalanceLineResponse from(TrialBalanceLine line) {
            return TrialBalanceLineResponse.builder()
                    .accountCode(line.getAccountCode())
                    .accountName(line.getAccountName())
                    .bsPlType(line.getBsplType())
                    .dcType(line.getDebitCreditType())
                    .openingBalance(line.getOpeningBalance())
                    .debitTotal(line.getDebitTotal())
                    .creditTotal(line.getCreditTotal())
                    .closingBalance(line.getClosingBalance())
                    .monthlyChange(line.getMonthlyChange())
                    .build();
        }
    }
}
