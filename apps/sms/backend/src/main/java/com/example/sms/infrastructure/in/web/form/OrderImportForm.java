package com.example.sms.infrastructure.in.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 受注取込フォーム.
 */
@Data
public class OrderImportForm {

    @NotNull(message = "ファイルを選択してください")
    private MultipartFile file;

    /**
     * 空行をスキップするか.
     */
    private boolean skipEmptyLines = true;

    /**
     * ヘッダー行をスキップするか.
     */
    private boolean skipHeaderLine = true;
}
