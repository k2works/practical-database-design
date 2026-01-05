package com.example.sms.domain.exception;

import java.math.BigDecimal;

/**
 * 在庫不足の場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class InsufficientInventoryException extends BusinessRuleViolationException {

    public InsufficientInventoryException(String productCode, BigDecimal available, BigDecimal requested) {
        super(String.format(
            "在庫が不足しています（商品: %s, 有効在庫: %s, 要求数量: %s）",
            productCode, available, requested
        ));
    }
}
