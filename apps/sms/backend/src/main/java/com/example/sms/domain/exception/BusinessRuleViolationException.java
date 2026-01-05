package com.example.sms.domain.exception;

/**
 * ビジネスルール違反の場合の基底例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public abstract class BusinessRuleViolationException extends RuntimeException {

    protected BusinessRuleViolationException(String message) {
        super(message);
    }
}
