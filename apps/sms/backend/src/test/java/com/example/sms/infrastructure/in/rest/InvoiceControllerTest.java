package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.InvoiceRepository;
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
 * 請求 API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("請求 API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class InvoiceControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/invoices")
    class GetAllInvoices {

        @Test
        @DisplayName("請求が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoInvoices() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoices")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/invoices/{invoiceNumber}")
    class GetInvoice {

        @Test
        @DisplayName("存在しない請求番号の場合は404を返す")
        void shouldReturn404WhenInvoiceNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/invoices/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("請求が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/invoices")
    class CreateInvoice {

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "invoiceDate": "2025-01-02"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/invoices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/invoices/{invoiceNumber}")
    class DeleteInvoice {

        @Test
        @DisplayName("存在しない請求番号の場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentInvoice() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/invoices/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }
}
