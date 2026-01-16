package com.example.pms.domain.exception;

/**
 * ドメイン例外の基底クラス.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
