package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.dto.JournalDetailResponse;
import com.example.fas.application.port.in.dto.JournalDetailResponse.DebitCreditDetailResponse;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.domain.model.common.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 仕訳画面コントローラーテスト.
 */
@WebMvcTest(JournalWebController.class)
@DisplayName("仕訳画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class JournalWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JournalUseCase journalUseCase;

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private DepartmentUseCase departmentUseCase;

    @Nested
    @DisplayName("GET /journals")
    class ListJournals {

        @Test
        @DisplayName("仕訳一覧画面を表示できる")
        void shouldDisplayJournalList() throws Exception {
            JournalResponse response = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            PageResult<JournalResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(journalUseCase.getJournals(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/journals"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("journals"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("日付範囲でフィルタできる")
        void shouldFilterByDateRange() throws Exception {
            JournalResponse response = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            PageResult<JournalResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            LocalDate from = LocalDate.of(2025, 1, 1);
            LocalDate to = LocalDate.of(2025, 1, 31);

            Mockito.when(journalUseCase.getJournals(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.eq(from),
                ArgumentMatchers.eq(to),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/journals")
                    .param("fromDate", "2025-01-01")
                    .param("toDate", "2025-01-31"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("fromDate", from))
                .andExpect(MockMvcResultMatchers.model().attribute("toDate", to));
        }

        @Test
        @DisplayName("キーワードでフィルタできる")
        void shouldFilterByKeyword() throws Exception {
            JournalResponse response = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            PageResult<JournalResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(journalUseCase.getJournals(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.eq("J0000")
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/journals")
                    .param("keyword", "J0000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "J0000"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            JournalResponse response = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            PageResult<JournalResponse> pageResult = new PageResult<>(List.of(response), 1, 10, 15);
            Mockito.when(journalUseCase.getJournals(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.eq(10),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/journals")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    @Nested
    @DisplayName("GET /journals/{voucherNumber}")
    class ShowJournal {

        @Test
        @DisplayName("仕訳詳細画面を表示できる")
        void shouldDisplayJournalDetail() throws Exception {
            JournalResponse response = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            Mockito.when(journalUseCase.getJournal("J00000001")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/journals/J00000001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("journal"));
        }
    }

    @Nested
    @DisplayName("GET /journals/new")
    class NewJournalForm {

        @Test
        @DisplayName("仕訳入力フォームを表示できる")
        void shouldDisplayNewJournalForm() throws Exception {
            Mockito.when(accountUseCase.getAllAccounts()).thenReturn(List.of());
            Mockito.when(departmentUseCase.getAllDepartments()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/journals/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("accounts"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"));
        }
    }

    @Nested
    @DisplayName("POST /journals")
    class CreateJournal {

        @Test
        @DisplayName("仕訳を登録できる")
        void shouldCreateJournal() throws Exception {
            JournalResponse created = createTestJournal("J00000001", LocalDate.of(2025, 1, 15));
            Mockito.when(journalUseCase.createJournal(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/journals")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("postingDate", "2025-01-15")
                    .param("voucherType", "NORMAL")
                    .param("lines[0].debitCreditType", "借方")
                    .param("lines[0].accountCode", "10100")
                    .param("lines[0].amount", "10000")
                    .param("lines[1].debitCreditType", "貸方")
                    .param("lines[1].accountCode", "40100")
                    .param("lines[1].amount", "10000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/journals/J00000001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            Mockito.when(accountUseCase.getAllAccounts()).thenReturn(List.of());
            Mockito.when(departmentUseCase.getAllDepartments()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.post("/journals")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("postingDate", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("journals/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("POST /journals/{voucherNumber}/cancel")
    class CancelJournal {

        @Test
        @DisplayName("仕訳を取消（赤黒処理）できる")
        void shouldCancelJournal() throws Exception {
            JournalResponse reversal = createTestJournal("J00000002", LocalDate.of(2025, 1, 15));
            Mockito.when(journalUseCase.cancelJournal("J00000001")).thenReturn(reversal);

            mockMvc.perform(MockMvcRequestBuilders.post("/journals/J00000001/cancel"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/journals/J00000001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /journals/{voucherNumber}/delete")
    class DeleteJournal {

        @Test
        @DisplayName("仕訳を削除できる")
        void shouldDeleteJournal() throws Exception {
            Mockito.doNothing().when(journalUseCase).deleteJournal("J00000001");

            mockMvc.perform(MockMvcRequestBuilders.post("/journals/J00000001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/journals"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private JournalResponse createTestJournal(String voucherNumber, LocalDate postingDate) {
        DebitCreditDetailResponse debitDetail = DebitCreditDetailResponse.builder()
            .debitCreditType("借方")
            .accountCode("10100")
            .amount(new BigDecimal("10000"))
            .build();

        DebitCreditDetailResponse creditDetail = DebitCreditDetailResponse.builder()
            .debitCreditType("貸方")
            .accountCode("40100")
            .amount(new BigDecimal("10000"))
            .build();

        JournalDetailResponse detail = JournalDetailResponse.builder()
            .lineNumber(1)
            .lineSummary("テスト仕訳")
            .debitCreditDetails(List.of(debitDetail, creditDetail))
            .build();

        return JournalResponse.builder()
            .journalVoucherNumber(voucherNumber)
            .postingDate(postingDate)
            .entryDate(postingDate)
            .voucherType("NORMAL")
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .periodicPostingFlag(false)
            .redSlipFlag(false)
            .debitTotal(new BigDecimal("10000"))
            .creditTotal(new BigDecimal("10000"))
            .details(List.of(detail))
            .build();
    }
}
