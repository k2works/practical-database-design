package com.example.fas.domain.model.autojournal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自動仕訳パターンマスタエンティティ.
 * 売上データから仕訳データへの変換ルールを定義する.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalPattern {
    private String patternCode;
    private String patternName;
    private String productGroup;
    private String customerGroup;
    private String salesType;
    private String debitAccountCode;
    private String debitSubAccountSetting;
    private String creditAccountCode;
    private String creditSubAccountSetting;
    private String returnDebitAccountCode;
    private String returnCreditAccountCode;
    private String taxProcessingType;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Integer version = 1;

    /**
     * 指定日付に有効かどうかを判定.
     *
     * @param date 判定対象日付
     * @return 有効期間内の場合 true
     */
    public boolean isValidAt(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validTo);
    }

    /**
     * 商品グループと顧客グループにマッチするか判定.
     *
     * @param productGroup 商品グループ
     * @param customerGroup 顧客グループ
     * @return マッチする場合 true
     */
    public boolean matches(String productGroup, String customerGroup) {
        boolean productMatch = "ALL".equals(this.productGroup)
                || this.productGroup.equals(productGroup);
        boolean customerMatch = "ALL".equals(this.customerGroup)
                || this.customerGroup.equals(customerGroup);
        return productMatch && customerMatch;
    }
}
