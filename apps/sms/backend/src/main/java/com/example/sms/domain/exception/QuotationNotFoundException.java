package com.example.sms.domain.exception;

/**
 * 見積が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class QuotationNotFoundException extends ResourceNotFoundException {

    public QuotationNotFoundException(String quotationNumber) {
        super("見積が見つかりません: " + quotationNumber);
    }

    public QuotationNotFoundException(Integer id) {
        super("見積が見つかりません: ID=" + id);
    }
}
