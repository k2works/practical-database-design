package com.example.fas.infrastructure.in.web;

import com.example.fas.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ホーム画面 Controller テスト.
 */
@AutoConfigureMockMvc
@DisplayName("ホーム画面")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class HomeControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / - ホーム画面を表示できる")
    void shouldDisplayHomePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("home"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("currentDate"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("accountCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("departmentCount"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("monthlyJournalCount"));
    }
}
