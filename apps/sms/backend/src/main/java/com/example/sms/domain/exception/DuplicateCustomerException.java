package com.example.sms.domain.exception;

/**
 * 顧客が既に存在する場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class DuplicateCustomerException extends DuplicateResourceException {

    public DuplicateCustomerException(String customerCode, String branchNumber) {
        super("顧客が既に存在します: " + customerCode + "-" + branchNumber);
    }
}
