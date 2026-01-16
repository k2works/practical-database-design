package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.defect.Defect;
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
import java.util.Optional;

/**
 * 欠点マスタ画面コントローラーテスト.
 */
@WebMvcTest(DefectWebController.class)
@DisplayName("欠点マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class DefectWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DefectUseCase defectUseCase;

    @Nested
    @DisplayName("GET /defects - 欠点一覧")
    class ListDefects {

        @Test
        @DisplayName("欠点一覧画面を表示できる")
        void shouldDisplayDefectList() throws Exception {
            Defect defect = createTestDefect("DEF-001", "キズ", "外観");
            PageResult<Defect> pageResult = new PageResult<>(List.of(defect), 0, 20, 1);
            Mockito.when(defectUseCase.getDefectList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defects"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("defectList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Defect defect = createTestDefect("DEF-001", "キズ", "外観");
            PageResult<Defect> pageResult = new PageResult<>(List.of(defect), 0, 20, 1);
            Mockito.when(defectUseCase.getDefectList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("キズ")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defects")
                    .param("keyword", "キズ"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "キズ"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Defect defect = createTestDefect("DEF-001", "キズ", "外観");
            PageResult<Defect> pageResult = new PageResult<>(List.of(defect), 1, 10, 25);
            Mockito.when(defectUseCase.getDefectList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/defects")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /defects/new - 欠点登録画面")
    class NewDefect {

        @Test
        @DisplayName("欠点登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/defects/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /defects - 欠点登録処理")
    class CreateDefect {

        @Test
        @DisplayName("欠点を登録できる")
        void shouldCreateDefect() throws Exception {
            Defect created = createTestDefect("DEF-001", "キズ", "外観");
            Mockito.when(defectUseCase.createDefect(
                    ArgumentMatchers.any(Defect.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/defects")
                    .param("defectCode", "DEF-001")
                    .param("defectName", "キズ")
                    .param("defectCategory", "外観"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defects"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/defects")
                    .param("defectCode", "")
                    .param("defectName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "defectCode"));
        }
    }

    @Nested
    @DisplayName("GET /defects/{defectCode} - 欠点詳細画面")
    class ShowDefect {

        @Test
        @DisplayName("欠点詳細画面を表示できる")
        void shouldDisplayDefectDetail() throws Exception {
            Defect defect = createTestDefect("DEF-001", "キズ", "外観");
            Mockito.when(defectUseCase.getDefect("DEF-001"))
                .thenReturn(Optional.of(defect));

            mockMvc.perform(MockMvcRequestBuilders.get("/defects/DEF-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("defect"));
        }

        @Test
        @DisplayName("欠点が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(defectUseCase.getDefect("DEF-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/defects/DEF-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defects"));
        }
    }

    @Nested
    @DisplayName("GET /defects/{defectCode}/edit - 欠点編集画面")
    class EditDefect {

        @Test
        @DisplayName("欠点編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Defect defect = createTestDefect("DEF-001", "キズ", "外観");
            Mockito.when(defectUseCase.getDefect("DEF-001"))
                .thenReturn(Optional.of(defect));

            mockMvc.perform(MockMvcRequestBuilders.get("/defects/DEF-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("defects/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("欠点が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(defectUseCase.getDefect("DEF-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/defects/DEF-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defects"));
        }
    }

    @Nested
    @DisplayName("POST /defects/{defectCode} - 欠点更新処理")
    class UpdateDefect {

        @Test
        @DisplayName("欠点を更新できる")
        void shouldUpdateDefect() throws Exception {
            Defect updated = createTestDefect("DEF-001", "大キズ", "外観");
            Mockito.when(defectUseCase.updateDefect(
                    ArgumentMatchers.eq("DEF-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/defects/DEF-001")
                    .param("defectCode", "DEF-001")
                    .param("defectName", "大キズ")
                    .param("defectCategory", "外観"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defects"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /defects/{defectCode}/delete - 欠点削除処理")
    class DeleteDefect {

        @Test
        @DisplayName("欠点を削除できる")
        void shouldDeleteDefect() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/defects/DEF-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/defects"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(defectUseCase).deleteDefect("DEF-001");
        }
    }

    private Defect createTestDefect(String defectCode, String defectName, String defectCategory) {
        return Defect.builder()
            .defectCode(defectCode)
            .defectName(defectName)
            .defectCategory(defectCategory)
            .build();
    }
}
