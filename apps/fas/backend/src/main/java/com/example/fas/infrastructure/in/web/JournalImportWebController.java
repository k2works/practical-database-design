package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.dto.JournalImportResult;
import com.example.fas.infrastructure.in.web.form.JournalImportForm;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 仕訳取込画面コントローラー.
 */
@Controller
@RequestMapping("/journals/import")
public class JournalImportWebController {

    private final JournalUseCase journalUseCase;

    public JournalImportWebController(JournalUseCase journalUseCase) {
        this.journalUseCase = journalUseCase;
    }

    /**
     * 仕訳取込画面を表示.
     */
    @GetMapping
    public String showImportForm(Model model) {
        model.addAttribute("form", new JournalImportForm());
        return "journals/import";
    }

    /**
     * サンプル CSV ファイルをダウンロード.
     */
    @GetMapping("/sample")
    public ResponseEntity<byte[]> downloadSampleCsv() {
        String sampleCsv = """
                起票日,貸借区分,勘定科目コード,補助科目コード,部門コード,金額,摘要
                2025/01/15,借方,11110,,10000,50000,現金売上
                2025/01/15,貸方,41110,,10000,50000,現金売上
                2025/01/16,借方,11110,,10000,30000,売掛金回収
                2025/01/16,貸方,11210,,10000,30000,売掛金回収
                2025/01/17,借方,62100,,10000,10000,旅費交通費
                2025/01/17,貸方,11110,,10000,10000,旅費交通費
                """;

        // BOM 付き UTF-8 で出力（Excel で文字化けしないように）
        byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] content = sampleCsv.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("journal_sample.csv", StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    /**
     * 仕訳を取り込む.
     */
    @PostMapping
    public String importJournals(
            @ModelAttribute("form") JournalImportForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // ファイルのバリデーション
        if (form.getFile() == null || form.getFile().isEmpty()) {
            bindingResult.rejectValue("file", "NotNull", "ファイルを選択してください");
            return "journals/import";
        }

        // ファイル形式のチェック
        String filename = form.getFile().getOriginalFilename();
        if (filename == null || !filename.toLowerCase(java.util.Locale.ROOT).endsWith(".csv")) {
            bindingResult.rejectValue("file", "InvalidFormat", "CSV ファイルを選択してください");
            return "journals/import";
        }

        return processImport(form, model, bindingResult);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private String processImport(JournalImportForm form, Model model, BindingResult bindingResult) {
        try {
            JournalImportResult result = journalUseCase.importJournalsFromCsv(
                    form.getFile().getInputStream(),
                    form.isSkipHeaderLine(),
                    form.isSkipEmptyLines());

            model.addAttribute("result", result);

            if (result.isSuccess()) {
                model.addAttribute("successMessage",
                        String.format("取込が完了しました（成功: %d件、スキップ: %d件）",
                                result.getSuccessCount(), result.getSkippedCount()));
            } else {
                model.addAttribute("warningMessage",
                        String.format("一部の取込に失敗しました（成功: %d件、エラー: %d件）",
                                result.getSuccessCount(), result.getErrorCount()));
            }

            return "journals/import";

        } catch (IOException e) {
            bindingResult.rejectValue("file", "ReadError", "ファイルの読み込みに失敗しました");
            return "journals/import";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "取込処理中にエラーが発生しました: " + e.getMessage());
            return "journals/import";
        }
    }
}
