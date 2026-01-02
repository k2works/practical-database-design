package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.ReceiptRepository;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 入金 API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("入金 API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ReceiptControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReceiptRepository receiptRepository;

    @BeforeEach
    void setUp() {
        receiptRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/receipts")
    class GetAllReceipts {

        @Test
        @DisplayName("入金が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoReceipts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/receipts")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/receipts/{receiptNumber}")
    class GetReceipt {

        @Test
        @DisplayName("存在しない入金番号の場合は404を返す")
        void shouldReturn404WhenReceiptNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/receipts/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("入金が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/receipts")
    class CreateReceipt {

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "receiptDate": "2025-01-02"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/receipts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/receipts/{receiptNumber}")
    class DeleteReceipt {

        @Test
        @DisplayName("存在しない入金番号の場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentReceipt() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/receipts/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }
}
