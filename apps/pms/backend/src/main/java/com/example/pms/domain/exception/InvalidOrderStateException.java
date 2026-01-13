package com.example.pms.domain.exception;

/**
 * 発注状態不正例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String orderNumber, String currentStatus, String expectedStatus) {
        super(String.format("発注状態が不正です: %s (現在: %s, 期待: %s)",
            orderNumber, currentStatus, expectedStatus));
    }
}
