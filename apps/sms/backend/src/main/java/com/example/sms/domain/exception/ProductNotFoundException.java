package com.example.sms.domain.exception;

/**
 * 商品が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ProductNotFoundException extends ResourceNotFoundException {

    public ProductNotFoundException(String productCode) {
        super("商品が見つかりません: " + productCode);
    }
}
