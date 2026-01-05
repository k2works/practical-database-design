package com.example.sms.domain.exception;

/**
 * 仕入先が見つからない場合の例外.
 */
public class SupplierNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SupplierNotFoundException(String supplierCode, String branchNumber) {
        super("仕入先が見つかりません: " + supplierCode + "-" + branchNumber);
    }
}
