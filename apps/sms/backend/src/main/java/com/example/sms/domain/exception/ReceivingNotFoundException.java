package com.example.sms.domain.exception;

/**
 * 入荷が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ReceivingNotFoundException extends ResourceNotFoundException {

    public ReceivingNotFoundException(String receivingNumber) {
        super("入荷が見つかりません: " + receivingNumber);
    }

    public ReceivingNotFoundException(Integer id) {
        super("入荷が見つかりません: ID=" + id);
    }
}
