package com.example.fas.domain.exception;

/**
 * 勘定科目が既に存在する場合の例外.
 */
public class AccountAlreadyExistsException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public AccountAlreadyExistsException(String accountCode) {
        super("ACC002", "勘定科目は既に存在します: " + accountCode);
    }
}
