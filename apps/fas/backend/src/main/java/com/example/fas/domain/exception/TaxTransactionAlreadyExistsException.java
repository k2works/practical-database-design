package com.example.fas.domain.exception;

/**
 * 課税取引が既に存在する場合の例外.
 */
public class TaxTransactionAlreadyExistsException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public TaxTransactionAlreadyExistsException(String taxCode) {
        super("TAX002", "課税取引は既に存在します: " + taxCode);
    }
}
