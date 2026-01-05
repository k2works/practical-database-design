package com.example.sms.domain.exception;

/**
 * リソースが重複している場合の基底例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public abstract class DuplicateResourceException extends RuntimeException {

    protected DuplicateResourceException(String message) {
        super(message);
    }
}
