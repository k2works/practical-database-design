package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.dto.JournalImportResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 仕訳取込画面コントローラーテスト.
 */
@WebMvcTest(JournalImportWebController.class)
@DisplayName("仕訳取込画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class JournalImportWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JournalUseCase journalUseCase;

    @Nested
    @DisplayName("GET /journals/import")
    class ShowImportForm {

        @Test
        @DisplayName("仕訳取込フォームを表示できる")
        void shouldDisplayImportForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/journals/import"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/import"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("GET /journals/import/sample")
    class DownloadSampleCsv {

        @Test
        @DisplayName("サンプル CSV ファイルをダウンロードできる")
        void shouldDownloadSampleCsv() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/journals/import/sample"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", "text/csv;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
        }
    }

    @Nested
    @DisplayName("POST /journals/import")
    class ImportJournals {

        @Test
        @DisplayName("CSV ファイルを取り込める")
        void shouldImportCsvFile() throws Exception {
            String csvContent = "起票日,貸借区分,勘定科目コード,補助科目コード,部門コード,金額,摘要\n"
                    + "2025/01/15,借方,11110,,10000,50000,テスト\n"
                    + "2025/01/15,貸方,41110,,10000,50000,テスト\n";

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "journals.csv",
                    "text/csv",
                    csvContent.getBytes(StandardCharsets.UTF_8));

            JournalImportResult result = JournalImportResult.builder()
                    .totalCount(1)
                    .successCount(1)
                    .skippedCount(0)
                    .errorCount(0)
                    .errors(List.of())
                    .build();

            Mockito.when(journalUseCase.importJournalsFromCsv(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(true),
                ArgumentMatchers.eq(true)
            )).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/journals/import")
                    .file(file)
                    .param("skipHeaderLine", "true")
                    .param("skipEmptyLines", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/import"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("result"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("ファイル未選択時はエラーになる")
        void shouldReturnErrorWhenFileIsEmpty() throws Exception {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file",
                    "",
                    "text/csv",
                    new byte[0]);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/journals/import")
                    .file(emptyFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/import"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }

        @Test
        @DisplayName("CSV 以外のファイルはエラーになる")
        void shouldReturnErrorWhenFileIsNotCsv() throws Exception {
            MockMultipartFile txtFile = new MockMultipartFile(
                    "file",
                    "journals.txt",
                    "text/plain",
                    "test content".getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(MockMvcRequestBuilders.multipart("/journals/import")
                    .file(txtFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/import"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }

        @Test
        @DisplayName("取込エラーがある場合は警告を表示する")
        void shouldDisplayWarningWhenImportHasErrors() throws Exception {
            String csvContent = "起票日,貸借区分,勘定科目コード,補助科目コード,部門コード,金額,摘要\n"
                    + "2025/01/15,借方,11110,,10000,50000,テスト\n"
                    + "2025/01/15,貸方,41110,,10000,50000,テスト\n"
                    + "invalid-date,借方,11110,,10000,50000,エラー行\n";

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "journals.csv",
                    "text/csv",
                    csvContent.getBytes(StandardCharsets.UTF_8));

            JournalImportResult result = JournalImportResult.builder()
                    .totalCount(2)
                    .successCount(1)
                    .skippedCount(0)
                    .errorCount(1)
                    .errors(List.of(
                            JournalImportResult.ImportError.builder()
                                    .lineNumber(3)
                                    .message("日付形式が不正です")
                                    .lineContent("invalid-date,借方,10100,,10000,50000,エラー行")
                                    .build()))
                    .build();

            Mockito.when(journalUseCase.importJournalsFromCsv(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(true),
                ArgumentMatchers.eq(true)
            )).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.multipart("/journals/import")
                    .file(file)
                    .param("skipHeaderLine", "true")
                    .param("skipEmptyLines", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/import"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("result"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("warningMessage"));
        }
    }
}
