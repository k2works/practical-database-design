package com.example.sms.domain.exception;

import java.math.BigDecimal;

/**
 * 与信限度額超過の場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class CreditLimitExceededException extends BusinessRuleViolationException {

    public CreditLimitExceededException(String customerCode, BigDecimal limit, BigDecimal amount) {
        super(String.format(
            "与信限度額を超過しています（顧客: %s, 限度額: %s, 申請額: %s）",
            customerCode, limit, amount
        ));
    }
}
