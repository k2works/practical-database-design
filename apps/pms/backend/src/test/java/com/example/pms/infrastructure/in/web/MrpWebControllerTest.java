package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.MrpUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MRP 実行画面コントローラーテスト.
 */
@WebMvcTest(MrpWebController.class)
@DisplayName("MRP 実行画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class MrpWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MrpUseCase mrpUseCase;

    @Nested
    @DisplayName("GET /mrp - MRP 実行画面")
    class Index {

        @Test
        @DisplayName("MRP 実行画面を表示できる")
        void shouldDisplayMrpForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/mrp"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mrp/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /mrp/execute - MRP 実行")
    class Execute {

        @Test
        @DisplayName("MRP を実行できる")
        void shouldExecuteMrp() throws Exception {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 31);

            MrpUseCase.MrpResult result = MrpUseCase.MrpResult.builder()
                .executionTime(LocalDateTime.of(2025, 1, 1, 10, 0, 0))
                .periodStart(startDate)
                .periodEnd(endDate)
                .plannedOrders(List.of(
                    MrpUseCase.PlannedOrder.builder()
                        .itemCode("MAT-001")
                        .itemName("テスト材料")
                        .orderType("PURCHASE")
                        .quantity(BigDecimal.valueOf(100))
                        .dueDate(LocalDate.of(2025, 1, 15))
                        .build()
                ))
                .shortageItems(List.of())
                .build();

            Mockito.when(mrpUseCase.execute(startDate, endDate)).thenReturn(result);

            mockMvc.perform(MockMvcRequestBuilders.post("/mrp/execute")
                    .param("startDate", "2025-01-01")
                    .param("endDate", "2025-01-31"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mrp/result"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("result"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/mrp/execute")
                    .param("startDate", "")
                    .param("endDate", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mrp/index"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "startDate", "endDate"));
        }
    }
}
