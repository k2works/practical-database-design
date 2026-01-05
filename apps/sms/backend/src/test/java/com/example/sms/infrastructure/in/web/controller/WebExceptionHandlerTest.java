package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Web 例外ハンドラーテスト.
 */
@AutoConfigureMockMvc
@Transactional
@DisplayName("Web 例外ハンドラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class WebExceptionHandlerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("ResourceNotFoundException のハンドリング")
    class ResourceNotFoundExceptionHandling {

        @Test
        @DisplayName("存在しない商品コードを指定すると404エラーページを表示する")
        void shouldDisplay404ErrorPage() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/products/UNKNOWN-CODE"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DataIntegrityViolationException のハンドリング")
    class DataIntegrityViolationExceptionHandling {

        @Test
        @DisplayName("存在しない顧客で受注登録するとビジネスエラーページを表示する")
        void shouldDisplayBusinessErrorPageForInvalidCustomer() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("orderDate", LocalDate.now().toString())
                    .param("customerCode", "INVALID-CUST")
                    .param("customerBranchNumber", "00")
                    .param("details[0].productCode", "PROD-001")
                    .param("details[0].productName", "テスト商品")
                    .param("details[0].orderQuantity", "10")
                    .param("details[0].unit", "個")
                    .param("details[0].unitPrice", "5000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error/business"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
        }
    }
}
