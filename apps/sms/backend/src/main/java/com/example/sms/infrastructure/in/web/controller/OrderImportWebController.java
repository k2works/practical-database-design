package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.dto.OrderImportResult;
import com.example.sms.infrastructure.in.web.form.OrderImportForm;
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

/**
 * 受注取込画面コントローラー.
 */
@Controller
@RequestMapping("/orders/import")
public class OrderImportWebController {

    private final OrderUseCase orderUseCase;

    public OrderImportWebController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    /**
     * 受注取込画面を表示.
     */
    @GetMapping
    public String showImportForm(Model model) {
        model.addAttribute("form", new OrderImportForm());
        return "orders/import";
    }

    /**
     * サンプル CSV ファイルをダウンロード.
     */
    @GetMapping("/sample")
    public ResponseEntity<byte[]> downloadSampleCsv() {
        String sampleCsv = """
                受注日,顧客コード,商品コード,数量,単価,納品希望日,備考
                2025/01/15,CUS-001,BEEF-001,10,1000,2025/01/20,サンプル受注1
                2025/01/16,CUS-001,BEEF-001,5,1000,2025/01/21,サンプル受注2
                2025/01/17,CUS-001,BEEF-001,20,1000,2025/01/22,サンプル受注3
                2025/01/18,CUS-001,BEEF-001,15,1000,2025/01/23,サンプル受注4
                2025/01/19,CUS-001,BEEF-001,8,1000,2025/01/24,サンプル受注5
                2025/01/20,CUS-001,BEEF-001,12,1000,2025/01/25,サンプル受注6
                2025/01/21,CUS-001,BEEF-001,30,1000,2025/01/26,サンプル受注7
                2025/01/22,CUS-001,BEEF-001,25,1000,2025/01/27,サンプル受注8
                2025/01/23,CUS-001,BEEF-001,18,1000,2025/01/28,サンプル受注9
                2025/01/24,CUS-001,BEEF-001,7,1000,2025/01/29,サンプル受注10
                """;

        // BOM 付き UTF-8 で出力（Excel で文字化けしないように）
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = sampleCsv.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("order_sample.csv", StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    /**
     * 受注を取り込む.
     */
    @PostMapping
    public String importOrders(
            @ModelAttribute("form") OrderImportForm form,
            BindingResult bindingResult,
            Model model) {

        // ファイルのバリデーション
        if (form.getFile() == null || form.getFile().isEmpty()) {
            bindingResult.rejectValue("file", "NotNull", "ファイルを選択してください");
            return "orders/import";
        }

        // ファイル形式のチェック
        String filename = form.getFile().getOriginalFilename();
        if (filename == null || !filename.toLowerCase(java.util.Locale.ROOT).endsWith(".csv")) {
            bindingResult.rejectValue("file", "InvalidFormat", "CSV ファイルを選択してください");
            return "orders/import";
        }

        return processImport(form, model, bindingResult);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private String processImport(OrderImportForm form, Model model, BindingResult bindingResult) {
        try {
            OrderImportResult result = orderUseCase.importOrdersFromCsv(
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

            return "orders/import";

        } catch (IOException e) {
            bindingResult.rejectValue("file", "ReadError", "ファイルの読み込みに失敗しました");
            return "orders/import";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "取込処理中にエラーが発生しました: " + e.getMessage());
            return "orders/import";
        }
    }
}
