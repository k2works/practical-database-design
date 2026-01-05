package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.SalesRepository;
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
 * 売上 API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("売上 API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class SalesControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SalesRepository salesRepository;

    @BeforeEach
    void setUp() {
        salesRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/sales")
    class GetAllSales {

        @Test
        @DisplayName("売上が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoSales() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sales")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/sales/{salesNumber}")
    class GetSales {

        @Test
        @DisplayName("存在しない売上番号の場合は404を返す")
        void shouldReturn404WhenSalesNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sales/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("売上が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/sales")
    class CreateSales {

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "salesDate": "2025-01-02"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/sales/{salesNumber}")
    class UpdateSales {

        @Test
        @DisplayName("存在しない売上番号の場合は404を返す")
        void shouldReturn404WhenUpdatingNonExistentSales() throws Exception {
            var request = """
                {
                    "remarks": "更新"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/sales/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/sales/{salesNumber}")
    class DeleteSales {

        @Test
        @DisplayName("存在しない売上番号の場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentSales() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/sales/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }
}
