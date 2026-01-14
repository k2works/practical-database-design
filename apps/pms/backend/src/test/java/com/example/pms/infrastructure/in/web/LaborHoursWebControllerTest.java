package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LaborHoursUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.LaborHours;
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
 * 工数実績画面コントローラーテスト.
 */
@WebMvcTest(LaborHoursWebController.class)
@DisplayName("工数実績画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class LaborHoursWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LaborHoursUseCase laborHoursUseCase;

    @MockitoBean
    private WorkOrderUseCase workOrderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private ProcessUseCase processUseCase;

    @MockitoBean
    private DepartmentUseCase departmentUseCase;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(workOrderUseCase.getAllWorkOrders()).thenReturn(Collections.emptyList());
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(processUseCase.getAllProcesses()).thenReturn(Collections.emptyList());
        Mockito.when(departmentUseCase.getAllDepartments()).thenReturn(Collections.emptyList());
        Mockito.when(staffUseCase.getAllStaff()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /labor-hours - 工数実績一覧")
    class ListLaborHours {

        @Test
        @DisplayName("工数実績一覧画面を表示できる")
        void shouldDisplayLaborHoursList() throws Exception {
            LaborHours laborHours = createTestLaborHours("LH-001", "WO-001");
            PageResult<LaborHours> pageResult = new PageResult<>(List.of(laborHours), 0, 20, 1);
            Mockito.when(laborHoursUseCase.getLaborHoursList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("laborHoursList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            LaborHours laborHours = createTestLaborHours("LH-001", "WO-001");
            PageResult<LaborHours> pageResult = new PageResult<>(List.of(laborHours), 0, 20, 1);
            Mockito.when(laborHoursUseCase.getLaborHoursList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            LaborHours laborHours = createTestLaborHours("LH-001", "WO-001");
            PageResult<LaborHours> pageResult = new PageResult<>(List.of(laborHours), 1, 10, 25);
            Mockito.when(laborHoursUseCase.getLaborHoursList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /labor-hours/new - 工数実績登録画面")
    class NewLaborHours {

        @Test
        @DisplayName("工数実績登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /labor-hours - 工数実績登録処理")
    class CreateLaborHours {

        @Test
        @DisplayName("工数実績を登録できる")
        void shouldCreateLaborHours() throws Exception {
            LaborHours created = createTestLaborHours("LH-001", "WO-001");
            Mockito.when(laborHoursUseCase.createLaborHours(
                    ArgumentMatchers.any(LaborHours.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/labor-hours")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("sequence", "1")
                    .param("processCode", "PROC-001")
                    .param("departmentCode", "DEPT-001")
                    .param("employeeCode", "EMP-001")
                    .param("workDate", "2024-01-20")
                    .param("hours", "8.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/labor-hours"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/labor-hours")
                    .param("workOrderNumber", "")
                    .param("hours", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "workOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /labor-hours/{laborHoursNumber} - 工数実績詳細画面")
    class ShowLaborHours {

        @Test
        @DisplayName("工数実績詳細画面を表示できる")
        void shouldDisplayLaborHoursDetail() throws Exception {
            LaborHours laborHours = createTestLaborHours("LH-001", "WO-001");
            Mockito.when(laborHoursUseCase.getLaborHours("LH-001"))
                .thenReturn(Optional.of(laborHours));

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours/LH-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("laborHours"));
        }

        @Test
        @DisplayName("工数実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(laborHoursUseCase.getLaborHours("LH-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours/LH-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/labor-hours"));
        }
    }

    @Nested
    @DisplayName("GET /labor-hours/{laborHoursNumber}/edit - 工数実績編集画面")
    class EditLaborHours {

        @Test
        @DisplayName("工数実績編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            LaborHours laborHours = createTestLaborHours("LH-001", "WO-001");
            Mockito.when(laborHoursUseCase.getLaborHours("LH-001"))
                .thenReturn(Optional.of(laborHours));

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours/LH-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("labor-hours/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("工数実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(laborHoursUseCase.getLaborHours("LH-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/labor-hours/LH-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/labor-hours"));
        }
    }

    @Nested
    @DisplayName("POST /labor-hours/{laborHoursNumber} - 工数実績更新処理")
    class UpdateLaborHours {

        @Test
        @DisplayName("工数実績を更新できる")
        void shouldUpdateLaborHours() throws Exception {
            LaborHours updated = createTestLaborHours("LH-001", "WO-001");
            Mockito.when(laborHoursUseCase.updateLaborHours(
                    ArgumentMatchers.eq("LH-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/labor-hours/LH-001")
                    .param("workOrderNumber", "WO-001")
                    .param("itemCode", "ITEM-001")
                    .param("sequence", "1")
                    .param("processCode", "PROC-001")
                    .param("departmentCode", "DEPT-001")
                    .param("employeeCode", "EMP-001")
                    .param("workDate", "2024-01-20")
                    .param("hours", "10.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/labor-hours"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /labor-hours/{laborHoursNumber}/delete - 工数実績削除処理")
    class DeleteLaborHours {

        @Test
        @DisplayName("工数実績を削除できる")
        void shouldDeleteLaborHours() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/labor-hours/LH-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/labor-hours"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(laborHoursUseCase).deleteLaborHours("LH-001");
        }
    }

    private LaborHours createTestLaborHours(String laborHoursNumber, String workOrderNumber) {
        return LaborHours.builder()
            .id(1)
            .laborHoursNumber(laborHoursNumber)
            .workOrderNumber(workOrderNumber)
            .itemCode("ITEM-001")
            .sequence(1)
            .processCode("PROC-001")
            .departmentCode("DEPT-001")
            .employeeCode("EMP-001")
            .workDate(LocalDate.of(2024, 1, 20))
            .hours(new BigDecimal("8.0"))
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
