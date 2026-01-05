package com.example.sms.domain.exception;

/**
 * 取引先が重複している場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class DuplicatePartnerException extends DuplicateResourceException {

    public DuplicatePartnerException(String partnerCode) {
        super("取引先が既に存在します: " + partnerCode);
    }
}
