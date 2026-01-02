package com.example.sms.domain.exception;

/**
 * 売上が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class SalesNotFoundException extends ResourceNotFoundException {

    public SalesNotFoundException(String salesNumber) {
        super("売上が見つかりません: " + salesNumber);
    }

    public SalesNotFoundException(Integer id) {
        super("売上が見つかりません: ID=" + id);
    }
}
