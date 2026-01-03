package com.example.sms.domain.exception;

/**
 * 支払が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PaymentNotFoundException extends ResourceNotFoundException {

    public PaymentNotFoundException(String paymentNumber) {
        super("支払が見つかりません: " + paymentNumber);
    }

    public PaymentNotFoundException(Integer id) {
        super("支払が見つかりません: ID=" + id);
    }
}
