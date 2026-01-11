package com.example.fas.domain.exception;

/**
 * 課税取引が見つからない場合の例外.
 */
public class TaxTransactionNotFoundException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public TaxTransactionNotFoundException(String taxCode) {
        super("TAX001", "課税取引が見つかりません: " + taxCode);
    }
}
