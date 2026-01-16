package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.StocktakingUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 棚卸画面コントローラーテスト.
 */
@WebMvcTest(StocktakingWebController.class)
@DisplayName("棚卸画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class StocktakingWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StocktakingUseCase stocktakingUseCase;

    @MockitoBean
    private LocationUseCase locationUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(locationUseCase.getAllLocations()).thenReturn(Collections.emptyList());
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /inventory-counts - 棚卸一覧")
    class ListStocktakings {

        @Test
        @DisplayName("棚卸一覧画面を表示できる")
        void shouldDisplayStocktakingList() throws Exception {
            Stocktaking stocktaking = createTestStocktaking("ST-001", "LOC-001");
            PageResult<Stocktaking> pageResult = new PageResult<>(List.of(stocktaking), 0, 20, 1);
            Mockito.when(stocktakingUseCase.getStocktakingList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("stocktakingList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Stocktaking stocktaking = createTestStocktaking("ST-001", "LOC-001");
            PageResult<Stocktaking> pageResult = new PageResult<>(List.of(stocktaking), 0, 20, 1);
            Mockito.when(stocktakingUseCase.getStocktakingList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("ST-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts")
                    .param("keyword", "ST-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "ST-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Stocktaking stocktaking = createTestStocktaking("ST-001", "LOC-001");
            PageResult<Stocktaking> pageResult = new PageResult<>(List.of(stocktaking), 1, 10, 25);
            Mockito.when(stocktakingUseCase.getStocktakingList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /inventory-counts/new - 棚卸登録画面")
    class NewStocktaking {

        @Test
        @DisplayName("棚卸登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("statusList"));
        }
    }

    @Nested
    @DisplayName("POST /inventory-counts - 棚卸登録処理")
    class CreateStocktaking {

        @Test
        @DisplayName("棚卸を登録できる")
        void shouldCreateStocktaking() throws Exception {
            Stocktaking created = createTestStocktaking("ST-001", "LOC-001");
            Mockito.when(stocktakingUseCase.createStocktaking(
                    ArgumentMatchers.any(Stocktaking.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/inventory-counts")
                    .param("locationCode", "LOC-001")
                    .param("stocktakingDate", "2024-01-20")
                    .param("status", "ISSUED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inventory-counts"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/inventory-counts")
                    .param("locationCode", "")
                    .param("stocktakingDate", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "locationCode"));
        }
    }

    @Nested
    @DisplayName("GET /inventory-counts/{stocktakingNumber} - 棚卸詳細画面")
    class ShowStocktaking {

        @Test
        @DisplayName("棚卸詳細画面を表示できる")
        void shouldDisplayStocktakingDetail() throws Exception {
            Stocktaking stocktaking = createTestStocktaking("ST-001", "LOC-001");
            Mockito.when(stocktakingUseCase.getStocktakingWithDetails("ST-001"))
                .thenReturn(Optional.of(stocktaking));

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts/ST-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("stocktaking"));
        }

        @Test
        @DisplayName("棚卸が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(stocktakingUseCase.getStocktakingWithDetails("ST-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts/ST-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inventory-counts"));
        }
    }

    @Nested
    @DisplayName("GET /inventory-counts/{stocktakingNumber}/edit - 棚卸編集画面")
    class EditStocktaking {

        @Test
        @DisplayName("棚卸編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Stocktaking stocktaking = createTestStocktaking("ST-001", "LOC-001");
            Mockito.when(stocktakingUseCase.getStocktakingWithDetails("ST-001"))
                .thenReturn(Optional.of(stocktaking));

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts/ST-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("inventory-counts/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("statusList"));
        }

        @Test
        @DisplayName("棚卸が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(stocktakingUseCase.getStocktakingWithDetails("ST-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/inventory-counts/ST-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inventory-counts"));
        }
    }

    @Nested
    @DisplayName("POST /inventory-counts/{stocktakingNumber} - 棚卸更新処理")
    class UpdateStocktaking {

        @Test
        @DisplayName("棚卸を更新できる")
        void shouldUpdateStocktaking() throws Exception {
            Stocktaking updated = createTestStocktaking("ST-001", "LOC-001");
            Mockito.when(stocktakingUseCase.updateStocktaking(
                    ArgumentMatchers.eq("ST-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/inventory-counts/ST-001")
                    .param("locationCode", "LOC-001")
                    .param("stocktakingDate", "2024-01-20")
                    .param("status", "ENTERED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inventory-counts"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /inventory-counts/{stocktakingNumber}/delete - 棚卸削除処理")
    class DeleteStocktaking {

        @Test
        @DisplayName("棚卸を削除できる")
        void shouldDeleteStocktaking() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/inventory-counts/ST-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/inventory-counts"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(stocktakingUseCase).deleteStocktaking("ST-001");
        }
    }

    private Stocktaking createTestStocktaking(String stocktakingNumber, String locationCode) {
        return Stocktaking.builder()
            .id(1)
            .stocktakingNumber(stocktakingNumber)
            .locationCode(locationCode)
            .stocktakingDate(LocalDate.of(2024, 1, 20))
            .status(StocktakingStatus.ISSUED)
            .version(1)
            .build();
    }
}
