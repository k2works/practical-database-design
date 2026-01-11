package com.example.fas.domain.exception;

/**
 * 仕訳が見つからない場合の例外.
 */
public class JournalNotFoundException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public JournalNotFoundException(String journalNumber) {
        super("JNL001", "仕訳が見つかりません: " + journalNumber);
    }
}
