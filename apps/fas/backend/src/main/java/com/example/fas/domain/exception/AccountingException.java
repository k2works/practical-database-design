package com.example.fas.domain.exception;

import lombok.Getter;

/**
 * 財務会計システムのドメイン例外基底クラス.
 */
@Getter
public abstract class AccountingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    protected AccountingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
