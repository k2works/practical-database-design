package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ProcessRouteUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;
import com.example.pms.domain.model.process.ProcessRoute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工程表画面コントローラーテスト.
 */
@WebMvcTest(ProcessRouteWebController.class)
@DisplayName("工程表画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProcessRouteWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessRouteUseCase processRouteUseCase;

    @MockitoBean
    private ProcessUseCase processUseCase;

    @Nested
    @DisplayName("GET /process-routes - 工程表一覧")
    class ListProcessRoutes {

        @Test
        @DisplayName("工程表一覧画面を表示できる")
        void shouldDisplayProcessRouteList() throws Exception {
            ProcessRoute route = createTestProcessRoute("ITEM-001", 1, "PROC-001");
            PageResult<ProcessRoute> pageResult = new PageResult<>(List.of(route), 0, 20, 1);
            Mockito.when(processRouteUseCase.getProcessRoutes(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/process-routes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("process-routes/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("routes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("品目コードでフィルタできる")
        void shouldFilterByItemCode() throws Exception {
            ProcessRoute route = createTestProcessRoute("ITEM-001", 1, "PROC-001");
            PageResult<ProcessRoute> pageResult = new PageResult<>(List.of(route), 0, 20, 1);
            Mockito.when(processRouteUseCase.getProcessRoutes(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("ITEM-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/process-routes")
                    .param("itemCode", "ITEM-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("process-routes/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("itemCode", "ITEM-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            ProcessRoute route = createTestProcessRoute("ITEM-001", 1, "PROC-001");
            PageResult<ProcessRoute> pageResult = new PageResult<>(List.of(route), 1, 10, 25);
            Mockito.when(processRouteUseCase.getProcessRoutes(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/process-routes")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /process-routes/new - 工程表登録画面")
    class NewProcessRoute {

        @Test
        @DisplayName("工程表登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            Process process = Process.builder()
                .processCode("PROC-001")
                .processName("組立工程")
                .build();
            Mockito.when(processUseCase.getAllProcesses()).thenReturn(List.of(process));

            mockMvc.perform(MockMvcRequestBuilders.get("/process-routes/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("process-routes/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("processes"));
        }
    }

    @Nested
    @DisplayName("POST /process-routes - 工程表登録処理")
    class CreateProcessRoute {

        @Test
        @DisplayName("工程表を登録できる")
        void shouldCreateProcessRoute() throws Exception {
            ProcessRoute created = createTestProcessRoute("ITEM-001", 1, "PROC-001");
            Mockito.when(processRouteUseCase.createProcessRoute(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/process-routes")
                    .param("itemCode", "ITEM-001")
                    .param("sequence", "1")
                    .param("processCode", "PROC-001"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/process-routes"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            Process process = Process.builder()
                .processCode("PROC-001")
                .processName("組立工程")
                .build();
            Mockito.when(processUseCase.getAllProcesses()).thenReturn(List.of(process));

            mockMvc.perform(MockMvcRequestBuilders.post("/process-routes")
                    .param("itemCode", "")
                    .param("sequence", "")
                    .param("processCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("process-routes/new"));
        }
    }

    private ProcessRoute createTestProcessRoute(String itemCode, int sequence, String processCode) {
        return ProcessRoute.builder()
            .itemCode(itemCode)
            .sequence(sequence)
            .processCode(processCode)
            .standardTime(BigDecimal.valueOf(10))
            .setupTime(BigDecimal.valueOf(5))
            .build();
    }
}
