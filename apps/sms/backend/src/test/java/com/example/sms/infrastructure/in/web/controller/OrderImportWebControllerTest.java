package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.dto.OrderImportResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 受注取込画面コントローラーのテスト.
 */
@WebMvcTest(OrderImportWebController.class)
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.TooManyStaticImports"})
class OrderImportWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @Nested
    @DisplayName("GET /orders/import")
    class ShowImportForm {

        @Test
        @DisplayName("取込画面を表示できる")
        void shouldShowImportForm() throws Exception {
            mockMvc.perform(get("/orders/import"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("orders/import"))
                    .andExpect(model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("GET /orders/import/sample")
    class DownloadSampleCsv {

        @Test
        @DisplayName("サンプル CSV をダウンロードできる")
        void shouldDownloadSampleCsv() throws Exception {
            mockMvc.perform(get("/orders/import/sample"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/csv"))
                    .andExpect(header().string("Content-Disposition",
                            org.hamcrest.Matchers.containsString("order_sample.csv")));
        }
    }

    @Nested
    @DisplayName("POST /orders/import")
    class ImportOrders {

        @Test
        @DisplayName("CSV ファイルを取り込める")
        void shouldImportOrders() throws Exception {
            String csvContent = """
                    受注日,顧客コード,商品コード,数量,単価,納品希望日,備考
                    2025/01/15,C001,P001,10,1000,2025/01/20,テスト
                    """;

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "orders.csv",
                    "text/csv",
                    csvContent.getBytes(StandardCharsets.UTF_8));

            OrderImportResult result = OrderImportResult.builder()
                    .totalCount(1)
                    .successCount(1)
                    .skippedCount(0)
                    .errorCount(0)
                    .errors(List.of())
                    .build();

            when(orderUseCase.importOrdersFromCsv(any(), anyBoolean(), anyBoolean()))
                    .thenReturn(result);

            mockMvc.perform(multipart("/orders/import")
                            .file(file)
                            .param("skipHeaderLine", "true")
                            .param("skipEmptyLines", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("orders/import"))
                    .andExpect(model().attributeExists("result"))
                    .andExpect(model().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("ファイル未選択時はエラーを表示する")
        void shouldShowErrorWhenNoFileSelected() throws Exception {
            mockMvc.perform(multipart("/orders/import")
                            .param("skipHeaderLine", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("orders/import"))
                    .andExpect(model().attributeHasFieldErrors("form", "file"));
        }

        @Test
        @DisplayName("CSV 以外のファイルはエラーを表示する")
        void shouldShowErrorForNonCsvFile() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "orders.txt",
                    "text/plain",
                    "test content".getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(multipart("/orders/import")
                            .file(file)
                            .param("skipHeaderLine", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("orders/import"))
                    .andExpect(model().attributeHasFieldErrors("form", "file"));
        }

        @Test
        @DisplayName("取込エラーがある場合は警告を表示する")
        void shouldShowWarningWhenImportHasErrors() throws Exception {
            String csvContent = "受注日,顧客コード,商品コード,数量,単価\n2025/01/15,C001,P001,10,1000";

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "orders.csv",
                    "text/csv",
                    csvContent.getBytes(StandardCharsets.UTF_8));

            OrderImportResult result = OrderImportResult.builder()
                    .totalCount(2)
                    .successCount(1)
                    .skippedCount(0)
                    .errorCount(1)
                    .errors(List.of(OrderImportResult.ImportError.builder()
                            .lineNumber(2)
                            .message("顧客コードが無効です")
                            .lineContent("2025/01/15,C002,P001,10,1000")
                            .build()))
                    .build();

            when(orderUseCase.importOrdersFromCsv(any(), anyBoolean(), anyBoolean()))
                    .thenReturn(result);

            mockMvc.perform(multipart("/orders/import")
                            .file(file)
                            .param("skipHeaderLine", "true"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("orders/import"))
                    .andExpect(model().attributeExists("result"))
                    .andExpect(model().attributeExists("warningMessage"));
        }
    }
}
