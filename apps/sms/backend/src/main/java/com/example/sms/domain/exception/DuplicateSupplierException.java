package com.example.sms.domain.exception;

/**
 * 仕入先が重複している場合の例外.
 */
public class DuplicateSupplierException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateSupplierException(String supplierCode, String branchNumber) {
        super("仕入先が既に存在します: " + supplierCode + "-" + branchNumber);
    }
}
