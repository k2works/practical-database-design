package com.example.sms.domain.exception;

/**
 * 顧客が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(String customerCode, String branchNumber) {
        super("顧客が見つかりません: " + customerCode + "-" + branchNumber);
    }
}
