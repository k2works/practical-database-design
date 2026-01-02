package com.example.sms.domain.exception;

/**
 * リソースが見つからない場合の基底例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public abstract class ResourceNotFoundException extends RuntimeException {

    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
