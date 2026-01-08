package com.example.fas.domain.exception;

import java.math.BigDecimal;

/**
 * 仕訳の貸借が一致しない場合の例外.
 */
public class JournalBalanceException extends AccountingException {

    private static final long serialVersionUID = 1L;

    public JournalBalanceException(BigDecimal debit, BigDecimal credit) {
        super("JNL002", String.format("貸借が一致しません。借方合計: %s, 貸方合計: %s", debit, credit));
    }
}
