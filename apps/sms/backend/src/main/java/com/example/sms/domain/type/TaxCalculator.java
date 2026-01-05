package com.example.sms.domain.type;

import com.example.sms.domain.model.product.TaxCategory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 消費税計算ロジック.
 */
@RequiredArgsConstructor
public class TaxCalculator {

    private final BigDecimal taxRate;

    /**
     * 消費税額を計算する.
     *
     * @param price 価格
     * @param taxCategory 税区分
     * @return 消費税額
     */
    public BigDecimal calculateTax(BigDecimal price, TaxCategory taxCategory) {
        return switch (taxCategory) {
            case EXCLUSIVE -> price.multiply(taxRate)
                    .setScale(0, RoundingMode.DOWN);
            case INCLUSIVE -> price.multiply(taxRate)
                    .divide(BigDecimal.ONE.add(taxRate), 0, RoundingMode.DOWN);
            case TAX_FREE -> BigDecimal.ZERO;
        };
    }

    /**
     * 税込金額を計算する.
     *
     * @param price 価格
     * @param taxCategory 税区分
     * @return 税込金額
     */
    public BigDecimal calculateTaxIncludedPrice(BigDecimal price, TaxCategory taxCategory) {
        return switch (taxCategory) {
            case EXCLUSIVE -> price.add(calculateTax(price, taxCategory));
            case INCLUSIVE -> price;
            case TAX_FREE -> price;
        };
    }

    /**
     * 税抜金額を計算する.
     *
     * @param taxIncludedPrice 税込価格
     * @param taxCategory 税区分
     * @return 税抜金額
     */
    public BigDecimal calculateTaxExcludedPrice(BigDecimal taxIncludedPrice, TaxCategory taxCategory) {
        return switch (taxCategory) {
            case EXCLUSIVE -> taxIncludedPrice;
            case INCLUSIVE -> taxIncludedPrice.divide(BigDecimal.ONE.add(taxRate), 0, RoundingMode.DOWN);
            case TAX_FREE -> taxIncludedPrice;
        };
    }
}
