package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.domain.model.partner.Partner;
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

import java.math.BigDecimal;

/**
 * 取引先マスタ API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("取引先マスタ API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class PartnerControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void setUp() {
        partnerRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/partners")
    class GetAllPartners {

        @Test
        @DisplayName("取引先一覧を取得できる")
        void shouldGetAllPartners() throws Exception {
            // Given
            Partner partner = createTestPartner("PTN-001", "テスト取引先");
            partnerRepository.save(partner);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].partnerCode").value("PTN-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].partnerName").value("テスト取引先"));
        }

        @Test
        @DisplayName("取引先が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoPartners() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("顧客のみをフィルタして取得できる")
        void shouldGetCustomersOnly() throws Exception {
            // Given
            Partner customer = createTestPartner("CUS-001", "顧客A");
            customer.setCustomer(true);
            partnerRepository.save(customer);

            Partner supplier = createTestPartner("SUP-001", "仕入先A");
            supplier.setSupplier(true);
            partnerRepository.save(supplier);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners")
                    .param("type", "customer")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].partnerCode").value("CUS-001"));
        }

        @Test
        @DisplayName("仕入先のみをフィルタして取得できる")
        void shouldGetSuppliersOnly() throws Exception {
            // Given
            Partner customer = createTestPartner("CUS-001", "顧客A");
            customer.setCustomer(true);
            partnerRepository.save(customer);

            Partner supplier = createTestPartner("SUP-001", "仕入先A");
            supplier.setSupplier(true);
            partnerRepository.save(supplier);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners")
                    .param("type", "supplier")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].partnerCode").value("SUP-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/partners/{partnerCode}")
    class GetPartner {

        @Test
        @DisplayName("取引先コードで取引先を取得できる")
        void shouldGetPartnerByCode() throws Exception {
            // Given
            Partner partner = createTestPartner("PTN-001", "テスト取引先");
            partnerRepository.save(partner);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners/PTN-001")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerCode").value("PTN-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerName").value("テスト取引先"));
        }

        @Test
        @DisplayName("存在しない取引先コードの場合は404を返す")
        void shouldReturn404WhenPartnerNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("取引先が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/partners")
    class CreatePartner {

        @Test
        @DisplayName("取引先を登録できる")
        void shouldCreatePartner() throws Exception {
            var request = """
                {
                    "partnerCode": "NEW-001",
                    "partnerName": "新規取引先",
                    "partnerNameKana": "シンキトリヒキサキ",
                    "isCustomer": true,
                    "isSupplier": false,
                    "postalCode": "100-0001",
                    "address1": "東京都千代田区",
                    "creditLimit": 1000000
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/partners")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerCode").value("NEW-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerName").value("新規取引先"));
        }

        @Test
        @DisplayName("取引先コードが重複している場合は409を返す")
        void shouldReturn409WhenPartnerCodeDuplicate() throws Exception {
            // Given
            Partner existing = createTestPartner("DUP-001", "既存取引先");
            partnerRepository.save(existing);

            var request = """
                {
                    "partnerCode": "DUP-001",
                    "partnerName": "重複取引先"
                }
                """;

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/partners")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("CONFLICT"));
        }

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "partnerName": "取引先名のみ"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/partners")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/partners/{partnerCode}")
    class UpdatePartner {

        @Test
        @DisplayName("取引先を更新できる")
        void shouldUpdatePartner() throws Exception {
            // Given
            Partner partner = createTestPartner("UPD-001", "更新前取引先");
            partnerRepository.save(partner);

            var request = """
                {
                    "partnerName": "更新後取引先",
                    "partnerNameKana": "コウシンゴトリヒキサキ",
                    "postalCode": "200-0002",
                    "address1": "大阪府大阪市"
                }
                """;

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/partners/UPD-001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerCode").value("UPD-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.partnerName").value("更新後取引先"));
        }

        @Test
        @DisplayName("存在しない取引先コードの場合は404を返す")
        void shouldReturn404WhenUpdatingNonExistentPartner() throws Exception {
            var request = """
                {
                    "partnerName": "更新取引先"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/partners/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/partners/{partnerCode}")
    class DeletePartner {

        @Test
        @DisplayName("取引先を削除できる")
        void shouldDeletePartner() throws Exception {
            // Given
            Partner partner = createTestPartner("DEL-001", "削除対象取引先");
            partnerRepository.save(partner);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/partners/DEL-001"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

            // Verify deletion
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/partners/DEL-001"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("存在しない取引先コードの場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentPartner() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/partners/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    private Partner createTestPartner(String partnerCode, String partnerName) {
        return Partner.builder()
            .partnerCode(partnerCode)
            .partnerName(partnerName)
            .partnerNameKana("テストトリヒキサキ")
            .isCustomer(false)
            .isSupplier(false)
            .postalCode("100-0001")
            .address1("東京都千代田区")
            .isTradingProhibited(false)
            .isMiscellaneous(false)
            .creditLimit(new BigDecimal("500000"))
            .temporaryCreditIncrease(BigDecimal.ZERO)
            .build();
    }
}
