package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;
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

import java.util.List;

/**
 * 工程マスタ画面コントローラーテスト.
 */
@WebMvcTest(ProcessWebController.class)
@DisplayName("工程マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProcessWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessUseCase processUseCase;

    @Nested
    @DisplayName("GET /processes - 工程一覧")
    class ListProcesses {

        @Test
        @DisplayName("工程一覧画面を表示できる")
        void shouldDisplayProcessList() throws Exception {
            Process process = createTestProcess("PROC-001", "組立工程");
            PageResult<Process> pageResult = new PageResult<>(List.of(process), 0, 20, 1);
            Mockito.when(processUseCase.getProcesses(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/processes"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("processes/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("processes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Process process = createTestProcess("PROC-001", "組立工程");
            PageResult<Process> pageResult = new PageResult<>(List.of(process), 0, 20, 1);
            Mockito.when(processUseCase.getProcesses(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("組立")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/processes")
                    .param("keyword", "組立"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("processes/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "組立"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Process process = createTestProcess("PROC-001", "組立工程");
            PageResult<Process> pageResult = new PageResult<>(List.of(process), 1, 10, 25);
            Mockito.when(processUseCase.getProcesses(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/processes")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /processes/{processCode} - 工程詳細")
    class ShowProcess {

        @Test
        @DisplayName("工程詳細画面を表示できる")
        void shouldDisplayProcessDetail() throws Exception {
            Process process = createTestProcess("PROC-001", "組立工程");
            Mockito.when(processUseCase.getProcess("PROC-001")).thenReturn(process);

            mockMvc.perform(MockMvcRequestBuilders.get("/processes/PROC-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("processes/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("process"));
        }
    }

    private Process createTestProcess(String code, String name) {
        return Process.builder()
            .processCode(code)
            .processName(name)
            .processType("加工")
            .locationCode("LOC-001")
            .build();
    }
}
