package com.example.fas.infrastructure.in.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 仕訳取込フォーム.
 */
@Data
public class JournalImportForm {

    @NotNull(message = "ファイルを選択してください")
    private MultipartFile file;

    /**
     * 重複時の処理方法.
     * SKIP: スキップ, REPLACE: 置換, ERROR: エラー
     */
    private String duplicateHandling = "SKIP";

    /**
     * 空行をスキップするか.
     */
    private boolean skipEmptyLines = true;

    /**
     * ヘッダー行をスキップするか.
     */
    private boolean skipHeaderLine = true;
}
