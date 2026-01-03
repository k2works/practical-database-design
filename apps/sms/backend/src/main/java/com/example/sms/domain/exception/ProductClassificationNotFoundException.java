package com.example.sms.domain.exception;

/**
 * 商品分類が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ProductClassificationNotFoundException extends ResourceNotFoundException {

    public ProductClassificationNotFoundException(String classificationCode) {
        super("商品分類が見つかりません: " + classificationCode);
    }
}
