package com.example.sms.domain.exception;

/**
 * 受注が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class SalesOrderNotFoundException extends ResourceNotFoundException {

    public SalesOrderNotFoundException(String orderNumber) {
        super("受注が見つかりません: " + orderNumber);
    }

    public SalesOrderNotFoundException(Integer id) {
        super("受注が見つかりません: ID=" + id);
    }
}
