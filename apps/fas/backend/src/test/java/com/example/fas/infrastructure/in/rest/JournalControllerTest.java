package com.example.fas.infrastructure.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.journal.JournalVoucherType;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * JournalController の統合テスト.
 */
@AutoConfigureMockMvc
@DisplayName("仕訳 API")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UseUnderscoresInNumericLiterals",
        "PMD.TooManyStaticImports"})
class JournalControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        journalRepository.deleteAll();
        accountRepository.deleteAll();

        // テスト用勘定科目を登録
        accountRepository.save(Account.builder()
                .accountCode("11110")
                .accountName("現金")
                .accountShortName("現金")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING)
                .build());
        accountRepository.save(Account.builder()
                .accountCode("21110")
                .accountName("買掛金")
                .accountShortName("買掛金")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.LIABILITY)
                .aggregationType(AggregationType.POSTING)
                .build());
    }

    private Journal createTestJournal(String voucherNumber) {
        JournalDebitCreditDetail debitDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("10000"))
                .build();

        JournalDebitCreditDetail creditDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("21110")
                .amount(new BigDecimal("10000"))
                .build();

        JournalDetail detail = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .lineSummary("テスト仕訳")
                .debitCreditDetails(List.of(debitDetail, creditDetail))
                .build();

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.of(2025, 1, 15))
                .entryDate(LocalDate.of(2025, 1, 15))
                .voucherType(JournalVoucherType.NORMAL)
                .details(List.of(detail))
                .build();
    }

    @Nested
    @DisplayName("GET /api/journals/{voucherNumber}")
    class GetJournalTest {

        @Test
        @DisplayName("仕訳を取得できる")
        void canGetJournal() throws Exception {
            // Given
            Journal journal = createTestJournal("J00001");
            journalRepository.save(journal);

            // When & Then
            mockMvc.perform(get("/api/journals/{voucherNumber}", "J00001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.journalVoucherNumber").value("J00001"))
                    .andExpect(jsonPath("$.postingDate").value("2025-01-15"))
                    .andExpect(jsonPath("$.debitTotal").value(10000))
                    .andExpect(jsonPath("$.creditTotal").value(10000));
        }

        @Test
        @DisplayName("存在しない仕訳は404を返す")
        void notFoundForNonExistent() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/journals/{voucherNumber}", "XXXXX"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/journals")
    class GetJournalsTest {

        @Test
        @DisplayName("期間指定で仕訳を取得できる")
        void canGetJournalsByDateRange() throws Exception {
            // Given
            Journal journal = createTestJournal("J00001");
            journalRepository.save(journal);

            // When & Then
            mockMvc.perform(get("/api/journals")
                            .param("fromDate", "2025-01-01")
                            .param("toDate", "2025-01-31"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].journalVoucherNumber").value("J00001"));
        }
    }

    @Nested
    @DisplayName("POST /api/journals")
    class CreateJournalTest {

        @Test
        @DisplayName("仕訳を登録できる")
        void canCreateJournal() throws Exception {
            // Given
            String json = """
                {
                    "postingDate": "2025-01-15",
                    "details": [
                        {
                            "lineSummary": "テスト仕訳",
                            "debitCreditDetails": [
                                {
                                    "debitCreditType": "借方",
                                    "accountCode": "11110",
                                    "amount": 10000
                                },
                                {
                                    "debitCreditType": "貸方",
                                    "accountCode": "21110",
                                    "amount": 10000
                                }
                            ]
                        }
                    ]
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/journals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.journalVoucherNumber").exists())
                    .andExpect(jsonPath("$.debitTotal").value(10000))
                    .andExpect(jsonPath("$.creditTotal").value(10000));
        }

        @Test
        @DisplayName("貸借不一致の場合は400を返す")
        void badRequestForUnbalanced() throws Exception {
            // Given
            String json = """
                {
                    "postingDate": "2025-01-15",
                    "details": [
                        {
                            "lineSummary": "テスト仕訳",
                            "debitCreditDetails": [
                                {
                                    "debitCreditType": "借方",
                                    "accountCode": "11110",
                                    "amount": 10000
                                },
                                {
                                    "debitCreditType": "貸方",
                                    "accountCode": "21110",
                                    "amount": 5000
                                }
                            ]
                        }
                    ]
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/journals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("バリデーションエラーで400を返す")
        void badRequestForValidationError() throws Exception {
            // Given
            String json = """
                {
                    "postingDate": null,
                    "details": []
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/journals")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/journals/{voucherNumber}/cancel")
    class CancelJournalTest {

        @Test
        @DisplayName("仕訳を取消できる")
        void canCancelJournal() throws Exception {
            // Given
            Journal journal = createTestJournal("J00001");
            journalRepository.save(journal);

            // When & Then
            mockMvc.perform(post("/api/journals/{voucherNumber}/cancel", "J00001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.redSlipFlag").value(true));
        }
    }

    @Nested
    @DisplayName("DELETE /api/journals/{voucherNumber}")
    class DeleteJournalTest {

        @Test
        @DisplayName("仕訳を削除できる")
        void canDeleteJournal() throws Exception {
            // Given
            Journal journal = createTestJournal("J00001");
            journalRepository.save(journal);

            // When & Then
            mockMvc.perform(delete("/api/journals/{voucherNumber}", "J00001"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("存在しない仕訳の削除は404を返す")
        void notFoundForNonExistent() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/journals/{voucherNumber}", "XXXXX"))
                    .andExpect(status().isNotFound());
        }
    }
}
