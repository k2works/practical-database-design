package com.example.fas.application.port.in.dto;

import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳明細レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalDetailResponse {
    private Integer lineNumber;
    private String lineSummary;
    private List<DebitCreditDetailResponse> debitCreditDetails;

    /**
     * ドメインモデルからレスポンス DTO を生成.
     *
     * @param detail 仕訳明細ドメインモデル
     * @return レスポンス DTO
     */
    public static JournalDetailResponse from(JournalDetail detail) {
        return JournalDetailResponse.builder()
                .lineNumber(detail.getLineNumber())
                .lineSummary(detail.getLineSummary())
                .debitCreditDetails(detail.getDebitCreditDetails().stream()
                        .map(DebitCreditDetailResponse::from)
                        .toList())
                .build();
    }

    /**
     * 借方・貸方明細レスポンス DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DebitCreditDetailResponse {
        private String debitCreditType;
        private String accountCode;
        private String subAccountCode;
        private String departmentCode;
        private java.math.BigDecimal amount;
        private String currencyCode;
        private java.math.BigDecimal exchangeRate;
        private java.math.BigDecimal baseCurrencyAmount;
        private String taxType;
        private Integer taxRate;
        private String taxCalcType;
        private java.time.LocalDate dueDate;
        private Boolean cashFlowFlag;

        /**
         * ドメインモデルからレスポンス DTO を生成.
         *
         * @param dc 借方・貸方明細
         * @return レスポンス DTO
         */
        public static DebitCreditDetailResponse from(JournalDebitCreditDetail dc) {
            return DebitCreditDetailResponse.builder()
                    .debitCreditType(dc.getDebitCreditType() != null
                            ? dc.getDebitCreditType().getDisplayName() : null)
                    .accountCode(dc.getAccountCode())
                    .subAccountCode(dc.getSubAccountCode())
                    .departmentCode(dc.getDepartmentCode())
                    .amount(dc.getAmount())
                    .currencyCode(dc.getCurrencyCode())
                    .exchangeRate(dc.getExchangeRate())
                    .baseCurrencyAmount(dc.getBaseCurrencyAmount())
                    .taxType(dc.getTaxType() != null ? dc.getTaxType().name() : null)
                    .taxRate(dc.getTaxRate())
                    .taxCalcType(dc.getTaxCalcType() != null
                            ? dc.getTaxCalcType().name() : null)
                    .dueDate(dc.getDueDate())
                    .cashFlowFlag(dc.getCashFlowFlag())
                    .build();
        }
    }
}
