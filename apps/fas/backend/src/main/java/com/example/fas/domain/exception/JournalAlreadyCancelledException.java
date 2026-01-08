package com.example.fas.domain.exception;

/**
 * 仕訳が既に取消済みの場合の例外.
 */
public class JournalAlreadyCancelledException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public JournalAlreadyCancelledException(String journalNumber) {
        super("JNL003", "仕訳は既に取消済みです: " + journalNumber);
    }
}
