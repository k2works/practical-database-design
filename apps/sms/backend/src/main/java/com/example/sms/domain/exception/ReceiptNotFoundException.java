package com.example.sms.domain.exception;

/**
 * 入金が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ReceiptNotFoundException extends ResourceNotFoundException {

    public ReceiptNotFoundException(String receiptNumber) {
        super("入金が見つかりません: " + receiptNumber);
    }

    public ReceiptNotFoundException(Integer id) {
        super("入金が見つかりません: ID=" + id);
    }
}
