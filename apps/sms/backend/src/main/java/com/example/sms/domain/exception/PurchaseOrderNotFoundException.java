package com.example.sms.domain.exception;

/**
 * 発注が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PurchaseOrderNotFoundException extends ResourceNotFoundException {

    public PurchaseOrderNotFoundException(String purchaseOrderNumber) {
        super("発注が見つかりません: " + purchaseOrderNumber);
    }

    public PurchaseOrderNotFoundException(Integer id) {
        super("発注が見つかりません: ID=" + id);
    }
}
