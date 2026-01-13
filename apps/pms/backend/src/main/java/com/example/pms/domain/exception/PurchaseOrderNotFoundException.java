package com.example.pms.domain.exception;

/**
 * 発注が見つからない例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PurchaseOrderNotFoundException extends DomainException {

    public PurchaseOrderNotFoundException(String orderNumber) {
        super("発注が見つかりません: " + orderNumber);
    }
}
