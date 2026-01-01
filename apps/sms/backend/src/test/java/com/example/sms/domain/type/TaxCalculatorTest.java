package com.example.sms.domain.type;

import com.example.sms.domain.model.product.TaxCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 消費税計算テスト.
 */
@DisplayName("消費税計算")
class TaxCalculatorTest {

    private TaxCalculator taxCalculator;

    @BeforeEach
    void setUp() {
        // 消費税率10%
        taxCalculator = new TaxCalculator(new BigDecimal("0.10"));
    }

    @Nested
    @DisplayName("消費税額の計算")
    class TaxAmount {

        @Test
        @DisplayName("外税の場合、価格に税率を掛けた金額が消費税額になる")
        void exclusiveTax() {
            var price = new BigDecimal("1000");
            var tax = taxCalculator.calculateTax(price, TaxCategory.EXCLUSIVE);
            assertThat(tax).isEqualByComparingTo(new BigDecimal("100"));
        }

        @Test
        @DisplayName("内税の場合、税込価格から消費税額を逆算する")
        void inclusiveTax() {
            var price = new BigDecimal("1100");
            var tax = taxCalculator.calculateTax(price, TaxCategory.INCLUSIVE);
            assertThat(tax).isEqualByComparingTo(new BigDecimal("100"));
        }

        @Test
        @DisplayName("非課税の場合、消費税額は0になる")
        void taxFree() {
            var price = new BigDecimal("1000");
            var tax = taxCalculator.calculateTax(price, TaxCategory.TAX_FREE);
            assertThat(tax).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("端数は切り捨てられる")
        void roundDown() {
            var price = new BigDecimal("999");
            var tax = taxCalculator.calculateTax(price, TaxCategory.EXCLUSIVE);
            assertThat(tax).isEqualByComparingTo(new BigDecimal("99"));
        }
    }

    @Nested
    @DisplayName("税込金額の計算")
    class TaxIncludedPrice {

        @Test
        @DisplayName("外税の場合、価格に消費税を加算した金額が税込金額になる")
        void exclusiveTax() {
            var price = new BigDecimal("1000");
            var taxIncluded = taxCalculator.calculateTaxIncludedPrice(price, TaxCategory.EXCLUSIVE);
            assertThat(taxIncluded).isEqualByComparingTo(new BigDecimal("1100"));
        }

        @Test
        @DisplayName("内税の場合、価格がそのまま税込金額になる")
        void inclusiveTax() {
            var price = new BigDecimal("1100");
            var taxIncluded = taxCalculator.calculateTaxIncludedPrice(price, TaxCategory.INCLUSIVE);
            assertThat(taxIncluded).isEqualByComparingTo(new BigDecimal("1100"));
        }

        @Test
        @DisplayName("非課税の場合、価格がそのまま税込金額になる")
        void taxFree() {
            var price = new BigDecimal("1000");
            var taxIncluded = taxCalculator.calculateTaxIncludedPrice(price, TaxCategory.TAX_FREE);
            assertThat(taxIncluded).isEqualByComparingTo(new BigDecimal("1000"));
        }
    }

    @Nested
    @DisplayName("税抜金額の計算")
    class TaxExcludedPrice {

        @Test
        @DisplayName("外税の場合、価格がそのまま税抜金額になる")
        void exclusiveTax() {
            var price = new BigDecimal("1000");
            var taxExcluded = taxCalculator.calculateTaxExcludedPrice(price, TaxCategory.EXCLUSIVE);
            assertThat(taxExcluded).isEqualByComparingTo(new BigDecimal("1000"));
        }

        @Test
        @DisplayName("内税の場合、税込価格から税抜金額を逆算する")
        void inclusiveTax() {
            var price = new BigDecimal("1100");
            var taxExcluded = taxCalculator.calculateTaxExcludedPrice(price, TaxCategory.INCLUSIVE);
            assertThat(taxExcluded).isEqualByComparingTo(new BigDecimal("1000"));
        }

        @Test
        @DisplayName("非課税の場合、価格がそのまま税抜金額になる")
        void taxFree() {
            var price = new BigDecimal("1000");
            var taxExcluded = taxCalculator.calculateTaxExcludedPrice(price, TaxCategory.TAX_FREE);
            assertThat(taxExcluded).isEqualByComparingTo(new BigDecimal("1000"));
        }
    }
}
