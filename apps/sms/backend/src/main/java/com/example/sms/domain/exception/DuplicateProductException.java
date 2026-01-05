package com.example.sms.domain.exception;

/**
 * 商品が重複している場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class DuplicateProductException extends DuplicateResourceException {

    public DuplicateProductException(String productCode) {
        super("商品が既に存在します: " + productCode);
    }
}
