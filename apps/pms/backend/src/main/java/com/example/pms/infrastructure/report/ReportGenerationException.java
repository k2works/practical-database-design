package com.example.pms.infrastructure.report;

/**
 * 帳票生成例外.
 */
public class ReportGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
