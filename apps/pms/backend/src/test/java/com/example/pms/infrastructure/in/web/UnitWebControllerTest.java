package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.UnitUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unit.Unit;
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
 * 単位マスタ画面コントローラーテスト.
 */
@WebMvcTest(UnitWebController.class)
@DisplayName("単位マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class UnitWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitUseCase unitUseCase;

    @Nested
    @DisplayName("GET /units - 単位一覧")
    class ListUnits {

        @Test
        @DisplayName("単位一覧画面を表示できる")
        void shouldDisplayUnitList() throws Exception {
            Unit unit = createTestUnit("PCS", "個", "個数");
            PageResult<Unit> pageResult = new PageResult<>(List.of(unit), 0, 20, 1);
            Mockito.when(unitUseCase.getUnits(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/units"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("units"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Unit unit = createTestUnit("KG", "kg", "キログラム");
            PageResult<Unit> pageResult = new PageResult<>(List.of(unit), 0, 20, 1);
            Mockito.when(unitUseCase.getUnits(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("キロ")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/units")
                    .param("keyword", "キロ"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "キロ"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Unit unit = createTestUnit("PCS", "個", "個数");
            PageResult<Unit> pageResult = new PageResult<>(List.of(unit), 1, 10, 25);
            Mockito.when(unitUseCase.getUnits(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/units")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /units/new - 単位登録画面")
    class NewUnit {

        @Test
        @DisplayName("単位登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/units/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /units - 単位登録処理")
    class CreateUnit {

        @Test
        @DisplayName("単位を登録できる")
        void shouldCreateUnit() throws Exception {
            Unit created = createTestUnit("NEW", "新", "新単位");
            Mockito.when(unitUseCase.createUnit(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/units")
                    .param("unitCode", "NEW")
                    .param("unitSymbol", "新")
                    .param("unitName", "新単位"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/units"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/units")
                    .param("unitCode", "")
                    .param("unitSymbol", "")
                    .param("unitName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "unitCode", "unitSymbol", "unitName"));
        }
    }

    @Nested
    @DisplayName("GET /units/{unitCode}/edit - 単位編集画面")
    class EditUnit {

        @Test
        @DisplayName("単位編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Unit unit = createTestUnit("PCS", "個", "個数");
            Mockito.when(unitUseCase.getUnit("PCS")).thenReturn(Optional.of(unit));

            mockMvc.perform(MockMvcRequestBuilders.get("/units/PCS/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("存在しない単位の場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(unitUseCase.getUnit("NOTFOUND")).thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/units/NOTFOUND/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/units"));
        }
    }

    @Nested
    @DisplayName("POST /units/{unitCode} - 単位更新処理")
    class UpdateUnit {

        @Test
        @DisplayName("単位を更新できる")
        void shouldUpdateUnit() throws Exception {
            Unit updated = createTestUnit("PCS", "個", "個数（更新）");
            Mockito.when(unitUseCase.updateUnit(ArgumentMatchers.eq("PCS"), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/units/PCS")
                    .param("unitCode", "PCS")
                    .param("unitSymbol", "個")
                    .param("unitName", "個数（更新）"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/units"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は編集画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/units/PCS")
                    .param("unitCode", "PCS")
                    .param("unitSymbol", "")
                    .param("unitName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("units/edit"));
        }
    }

    @Nested
    @DisplayName("POST /units/{unitCode}/delete - 単位削除処理")
    class DeleteUnit {

        @Test
        @DisplayName("単位を削除できる")
        void shouldDeleteUnit() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/units/PCS/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/units"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(unitUseCase).deleteUnit("PCS");
        }
    }

    private Unit createTestUnit(String code, String symbol, String name) {
        return Unit.builder()
            .unitCode(code)
            .unitSymbol(symbol)
            .unitName(name)
            .build();
    }
}
