package com.example.fas.domain.exception;

/**
 * 勘定科目構成が見つからない場合の例外.
 */
public class AccountStructureNotFoundException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public AccountStructureNotFoundException(String accountCode) {
        super("AST001", "勘定科目構成が見つかりません: " + accountCode);
    }
}
