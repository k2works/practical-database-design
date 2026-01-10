package com.example.sms.application.port.in.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * 受注取込結果 DTO.
 */
@Value
@Builder
public class OrderImportResult {

    int totalCount;
    int successCount;
    int skippedCount;
    int errorCount;
    List<ImportError> errors;

    /**
     * 取込が完全に成功したかどうか.
     *
     * @return エラーがなければ true
     */
    public boolean isSuccess() {
        return errorCount == 0;
    }

    /**
     * 取込エラー情報.
     */
    @Value
    @Builder
    public static class ImportError {
        int lineNumber;
        String message;
        String lineContent;
    }
}
