package com.example.fas.application.port.in.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕訳取込結果 DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalImportResult {

    /**
     * 処理件数.
     */
    private int totalCount;

    /**
     * 成功件数.
     */
    private int successCount;

    /**
     * スキップ件数.
     */
    private int skippedCount;

    /**
     * エラー件数.
     */
    private int errorCount;

    /**
     * エラー詳細リスト.
     */
    @Builder.Default
    private List<ImportError> errors = new ArrayList<>();

    /**
     * 取込エラー情報.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int lineNumber;
        private String message;
        private String lineContent;
    }

    /**
     * 成功したかどうか.
     *
     * @return エラーがなければ true
     */
    public boolean isSuccess() {
        return errorCount == 0;
    }
}
