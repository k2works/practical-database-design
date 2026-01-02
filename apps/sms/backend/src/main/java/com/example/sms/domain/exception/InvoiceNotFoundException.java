package com.example.sms.domain.exception;

/**
 * 請求が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class InvoiceNotFoundException extends ResourceNotFoundException {

    public InvoiceNotFoundException(String invoiceNumber) {
        super("請求が見つかりません: " + invoiceNumber);
    }

    public InvoiceNotFoundException(Integer id) {
        super("請求が見つかりません: ID=" + id);
    }
}
