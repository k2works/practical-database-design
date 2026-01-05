package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.ShipmentRepository;
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
 * 出荷 API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("出荷 API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ShipmentControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @BeforeEach
    void setUp() {
        shipmentRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/shipments")
    class GetAllShipments {

        @Test
        @DisplayName("出荷が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoShipments() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shipments")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/shipments/{shipmentNumber}")
    class GetShipment {

        @Test
        @DisplayName("存在しない出荷番号の場合は404を返す")
        void shouldReturn404WhenShipmentNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shipments/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("出荷が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/shipments")
    class CreateShipment {

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "shipmentDate": "2025-01-02"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shipments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/shipments/{shipmentNumber}")
    class UpdateShipment {

        @Test
        @DisplayName("存在しない出荷番号の場合は404を返す")
        void shouldReturn404WhenUpdatingNonExistentShipment() throws Exception {
            var request = """
                {
                    "remarks": "更新"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shipments/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/shipments/{shipmentNumber}")
    class DeleteShipment {

        @Test
        @DisplayName("存在しない出荷番号の場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentShipment() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/shipments/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }
}
