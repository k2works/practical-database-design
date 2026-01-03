package com.example.sms.domain.exception;

/**
 * 仕入が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PurchaseNotFoundException extends ResourceNotFoundException {

    public PurchaseNotFoundException(String purchaseNumber) {
        super("仕入が見つかりません: " + purchaseNumber);
    }

    public PurchaseNotFoundException(Integer id) {
        super("仕入が見つかりません: ID=" + id);
    }
}
