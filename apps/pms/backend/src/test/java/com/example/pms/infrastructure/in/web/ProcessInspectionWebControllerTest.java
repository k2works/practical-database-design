package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.ProcessInspectionUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ProcessInspection;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 不良管理（工程検査）画面コントローラーテスト.
 */
@WebMvcTest(ProcessInspectionWebController.class)
@DisplayName("不良管理画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProcessInspectionWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessInspectionUseCase processInspectionUseCase;

    @MockitoBean
    private WorkOrderUseCase workOrderUseCase;

    @MockitoBean
    private ProcessUseCase processUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private DefectUseCase defectUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(workOrderUseCase.getAllWorkOrders()).thenReturn(Collections.emptyList());
        Mockito.when(processUseCase.getAllProcesses()).thenReturn(Collections.emptyList());
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(defectUseCase.getAllDefects()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /defect-management - 不良管理一覧")
    class ListProcessInspections {

        @Test
        @DisplayName("不良管理一覧画面を表示できる")
        void shouldDisplayProcessInspectionList() throws Exception {
            ProcessInspection inspection = createTestProcessInspection("PI-001", "WO-001");
            PageResult<ProcessInspection> pageResult = new PageResult<>(List.of(inspection), 0, 20, 1);
            Mockito.when(processInspectionUseCase.getProcessInspectionList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspectionList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            ProcessInspection inspection = createTestProcessInspection("PI-001", "WO-001");
            PageResult<ProcessInspection> pageResult = new PageResult<>(List.of(inspection), 0, 20, 1);
            Mockito.when(processInspectionUseCase.getProcessInspectionList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            ProcessInspection inspection = createTestProcessInspection("PI-001", "WO-001");
            PageResult<ProcessInspection> pageResult = new PageResult<>(List.of(inspection), 1, 10, 25);
            Mockito.when(processInspectionUseCase.getProcessInspectionList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /defect-management/new - 不良管理登録画面")
    class NewProcessInspection {

        @Test
        @DisplayName("不良管理登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("processes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"));
        }
    }

    @Nested
    @DisplayName("POST /defect-management - 不良管理登録処理")
    class CreateProcessInspection {

        @Test
        @DisplayName("不良管理を登録できる")
        void shouldCreateProcessInspection() throws Exception {
            ProcessInspection created = createTestProcessInspection("PI-001", "WO-001");
            Mockito.when(processInspectionUseCase.createProcessInspection(
                    ArgumentMatchers.any(ProcessInspection.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/defect-management")
                    .param("workOrderNumber", "WO-001")
                    .param("processCode", "PROC-001")
                    .param("itemCode", "ITEM-001")
                    .param("inspectionDate", "2025-01-14")
                    .param("inspectorCode", "STAFF-001")
                    .param("inspectionQuantity", "100")
                    .param("passedQuantity", "95")
                    .param("failedQuantity", "5")
                    .param("judgment", "PASSED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defect-management"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/defect-management")
                    .param("workOrderNumber", "")
                    .param("itemCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "workOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /defect-management/{inspectionNumber} - 不良管理詳細画面")
    class ShowProcessInspection {

        @Test
        @DisplayName("不良管理詳細画面を表示できる")
        void shouldDisplayProcessInspectionDetail() throws Exception {
            ProcessInspection inspection = createTestProcessInspection("PI-001", "WO-001");
            Mockito.when(processInspectionUseCase.getProcessInspection("PI-001"))
                .thenReturn(Optional.of(inspection));

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management/PI-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspection"));
        }

        @Test
        @DisplayName("不良管理が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(processInspectionUseCase.getProcessInspection("PI-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management/PI-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defect-management"));
        }
    }

    @Nested
    @DisplayName("GET /defect-management/{inspectionNumber}/edit - 不良管理編集画面")
    class EditProcessInspection {

        @Test
        @DisplayName("不良管理編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            ProcessInspection inspection = createTestProcessInspection("PI-001", "WO-001");
            Mockito.when(processInspectionUseCase.getProcessInspection("PI-001"))
                .thenReturn(Optional.of(inspection));

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management/PI-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defect-management/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrders"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("processes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"));
        }

        @Test
        @DisplayName("不良管理が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(processInspectionUseCase.getProcessInspection("PI-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/defect-management/PI-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defect-management"));
        }
    }

    @Nested
    @DisplayName("POST /defect-management/{inspectionNumber} - 不良管理更新処理")
    class UpdateProcessInspection {

        @Test
        @DisplayName("不良管理を更新できる")
        void shouldUpdateProcessInspection() throws Exception {
            ProcessInspection updated = createTestProcessInspection("PI-001", "WO-001");
            Mockito.when(processInspectionUseCase.updateProcessInspection(
                    ArgumentMatchers.eq("PI-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/defect-management/PI-001")
                    .param("inspectionNumber", "PI-001")
                    .param("workOrderNumber", "WO-001")
                    .param("processCode", "PROC-001")
                    .param("itemCode", "ITEM-001")
                    .param("inspectionDate", "2025-01-14")
                    .param("inspectorCode", "STAFF-001")
                    .param("inspectionQuantity", "100")
                    .param("passedQuantity", "90")
                    .param("failedQuantity", "10")
                    .param("judgment", "FAILED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defect-management"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /defect-management/{inspectionNumber}/delete - 不良管理削除処理")
    class DeleteProcessInspection {

        @Test
        @DisplayName("不良管理を削除できる")
        void shouldDeleteProcessInspection() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/defect-management/PI-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defect-management"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(processInspectionUseCase).deleteProcessInspection("PI-001");
        }
    }

    private ProcessInspection createTestProcessInspection(String inspectionNumber, String workOrderNumber) {
        return ProcessInspection.builder()
            .id(1)
            .inspectionNumber(inspectionNumber)
            .workOrderNumber(workOrderNumber)
            .processCode("PROC-001")
            .itemCode("ITEM-001")
            .inspectionDate(LocalDate.of(2025, 1, 14))
            .inspectorCode("STAFF-001")
            .inspectionQuantity(new BigDecimal("100"))
            .passedQuantity(new BigDecimal("95"))
            .failedQuantity(new BigDecimal("5"))
            .judgment(InspectionJudgment.PASSED)
            .build();
    }
}
