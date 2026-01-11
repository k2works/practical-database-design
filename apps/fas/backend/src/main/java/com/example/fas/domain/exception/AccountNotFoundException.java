package com.example.fas.domain.exception;

/**
 * 勘定科目が見つからない場合の例外.
 */
public class AccountNotFoundException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String accountCode) {
        super("ACC001", "勘定科目が見つかりません: " + accountCode);
    }
}
