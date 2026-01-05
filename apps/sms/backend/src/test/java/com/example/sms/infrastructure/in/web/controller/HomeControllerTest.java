package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ホーム画面コントローラーテスト.
 */
@AutoConfigureMockMvc
@DisplayName("ホーム画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class HomeControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("ホーム画面を表示できる")
    void shouldDisplayHomePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("index"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("productCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("partnerCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("pendingOrderCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("pendingShipmentCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("unpaidInvoiceCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("todayReceiptCount"));
    }
}
