package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.in.InspectionResultUseCase;
import com.example.pms.application.port.out.DefectRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.InspectionResult;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 検査実績画面コントローラーテスト.
 */
@WebMvcTest(InspectionResultWebController.class)
@DisplayName("検査実績画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class InspectionResultWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InspectionResultUseCase inspectionResultUseCase;

    @MockitoBean
    private CompletionResultUseCase completionResultUseCase;

    @MockitoBean
    private DefectRepository defectRepository;

    @BeforeEach
    void setUp() {
        Mockito.when(completionResultUseCase.getAllCompletionResults()).thenReturn(Collections.emptyList());
        Mockito.when(defectRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /inspection-results - 検査実績一覧")
    class ListInspectionResults {

        @Test
        @DisplayName("検査実績一覧画面を表示できる")
        void shouldDisplayInspectionResultList() throws Exception {
            InspectionResult inspectionResult = createTestInspectionResult(1, "CR-001", "DEF-001");
            PageResult<InspectionResult> pageResult = new PageResult<>(List.of(inspectionResult), 0, 20, 1);
            Mockito.when(inspectionResultUseCase.getInspectionResultList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspectionResultList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            InspectionResult inspectionResult = createTestInspectionResult(1, "CR-001", "DEF-001");
            PageResult<InspectionResult> pageResult = new PageResult<>(List.of(inspectionResult), 0, 20, 1);
            Mockito.when(inspectionResultUseCase.getInspectionResultList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("CR-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results")
                    .param("keyword", "CR-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "CR-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            InspectionResult inspectionResult = createTestInspectionResult(1, "CR-001", "DEF-001");
            PageResult<InspectionResult> pageResult = new PageResult<>(List.of(inspectionResult), 1, 10, 25);
            Mockito.when(inspectionResultUseCase.getInspectionResultList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /inspection-results/new - 検査実績登録画面")
    class NewInspectionResult {

        @Test
        @DisplayName("検査実績登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("completionResults"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("defects"));
        }
    }

    @Nested
    @DisplayName("POST /inspection-results - 検査実績登録処理")
    class CreateInspectionResult {

        @Test
        @DisplayName("検査実績を登録できる")
        void shouldCreateInspectionResult() throws Exception {
            InspectionResult created = createTestInspectionResult(1, "CR-001", "DEF-001");
            Mockito.when(inspectionResultUseCase.createInspectionResult(
                    ArgumentMatchers.any(InspectionResult.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/inspection-results")
                    .param("completionResultNumber", "CR-001")
                    .param("defectCode", "DEF-001")
                    .param("quantity", "10"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inspection-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/inspection-results")
                    .param("completionResultNumber", "")
                    .param("defectCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "completionResultNumber"));
        }
    }

    @Nested
    @DisplayName("GET /inspection-results/{id} - 検査実績詳細画面")
    class ShowInspectionResult {

        @Test
        @DisplayName("検査実績詳細画面を表示できる")
        void shouldDisplayInspectionResultDetail() throws Exception {
            InspectionResult inspectionResult = createTestInspectionResult(1, "CR-001", "DEF-001");
            Mockito.when(inspectionResultUseCase.getInspectionResult(1))
                .thenReturn(Optional.of(inspectionResult));

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspectionResult"));
        }

        @Test
        @DisplayName("検査実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(inspectionResultUseCase.getInspectionResult(999))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results/999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inspection-results"));
        }
    }

    @Nested
    @DisplayName("GET /inspection-results/{id}/edit - 検査実績編集画面")
    class EditInspectionResult {

        @Test
        @DisplayName("検査実績編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            InspectionResult inspectionResult = createTestInspectionResult(1, "CR-001", "DEF-001");
            Mockito.when(inspectionResultUseCase.getInspectionResult(1))
                .thenReturn(Optional.of(inspectionResult));

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results/1/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inspection-results/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("completionResults"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("defects"));
        }

        @Test
        @DisplayName("検査実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(inspectionResultUseCase.getInspectionResult(999))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/inspection-results/999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inspection-results"));
        }
    }

    @Nested
    @DisplayName("POST /inspection-results/{id} - 検査実績更新処理")
    class UpdateInspectionResult {

        @Test
        @DisplayName("検査実績を更新できる")
        void shouldUpdateInspectionResult() throws Exception {
            InspectionResult updated = createTestInspectionResult(1, "CR-001", "DEF-001");
            Mockito.when(inspectionResultUseCase.updateInspectionResult(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/inspection-results/1")
                    .param("id", "1")
                    .param("completionResultNumber", "CR-001")
                    .param("defectCode", "DEF-002")
                    .param("quantity", "15"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inspection-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /inspection-results/{id}/delete - 検査実績削除処理")
    class DeleteInspectionResult {

        @Test
        @DisplayName("検査実績を削除できる")
        void shouldDeleteInspectionResult() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/inspection-results/1/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inspection-results"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(inspectionResultUseCase).deleteInspectionResult(1);
        }
    }

    private InspectionResult createTestInspectionResult(Integer id, String completionResultNumber, String defectCode) {
        return InspectionResult.builder()
            .id(id)
            .completionResultNumber(completionResultNumber)
            .defectCode(defectCode)
            .quantity(BigDecimal.TEN)
            .build();
    }
}
