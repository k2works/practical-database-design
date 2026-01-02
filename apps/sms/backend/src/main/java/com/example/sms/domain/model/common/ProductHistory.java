package com.example.sms.domain.model.common;

import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品マスタ履歴.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHistory {
    /** ID. */
    private Integer id;
    /** 商品コード. */
    private String productCode;
    /** 有効開始日. */
    private LocalDate validFromDate;
    /** 有効終了日. */
    private LocalDate validToDate;
    /** 商品名. */
    private String productName;
    /** 商品区分. */
    private ProductCategory productCategory;
    /** 単価. */
    private BigDecimal unitPrice;
    /** 税区分. */
    private TaxCategory taxCategory;
    /** 作成日時. */
    private LocalDateTime createdAt;
    /** 作成者. */
    private String createdBy;

    /**
     * 指定日時点で有効かどうか.
     *
     * @param targetDate 対象日
     * @return 有効な場合true
     */
    public boolean isValidOn(LocalDate targetDate) {
        if (targetDate.isBefore(validFromDate)) {
            return false;
        }
        return validToDate == null || !targetDate.isAfter(validToDate);
    }
}
