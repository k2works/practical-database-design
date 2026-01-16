package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
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
import java.util.List;

/**
 * 品目マスタ画面コントローラーテスト.
 */
@WebMvcTest(ItemWebController.class)
@DisplayName("品目マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ItemWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @Nested
    @DisplayName("GET /items - 品目一覧")
    class ListItems {

        @Test
        @DisplayName("品目一覧画面を表示できる")
        void shouldDisplayItemList() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品", ItemCategory.PRODUCT);
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"));
        }

        @Test
        @DisplayName("品目区分でフィルタできる")
        void shouldFilterByCategory() throws Exception {
            Item product = createTestItem("PROD-001", "製品A", ItemCategory.PRODUCT);
            PageResult<Item> pageResult = new PageResult<>(List.of(product), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq(ItemCategory.PRODUCT),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/items")
                    .param("category", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedCategory", ItemCategory.PRODUCT));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品", ItemCategory.PRODUCT);
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("テスト")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/items")
                    .param("keyword", "テスト"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "テスト"));
        }

        @Test
        @DisplayName("ページネーションパラメータが渡される")
        void shouldPassPaginationParams() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品", ItemCategory.PRODUCT);
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 2, 50, 150);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(2),
                    ArgumentMatchers.eq(50),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/items")
                    .param("page", "2")
                    .param("size", "50"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 50));
        }
    }

    @Nested
    @DisplayName("GET /items/{itemCode} - 品目詳細")
    class ShowItem {

        @Test
        @DisplayName("品目詳細画面を表示できる")
        void shouldDisplayItemDetail() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品", ItemCategory.PRODUCT);
            Mockito.when(itemUseCase.getItem("PROD-001")).thenReturn(item);

            mockMvc.perform(MockMvcRequestBuilders.get("/items/PROD-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("item"));
        }

        @Test
        @DisplayName("存在しない品目は404エラー")
        void shouldReturn404WhenNotFound() throws Exception {
            Mockito.when(itemUseCase.getItem("NOT-EXIST"))
                .thenThrow(new ItemNotFoundException("NOT-EXIST"));

            mockMvc.perform(MockMvcRequestBuilders.get("/items/NOT-EXIST"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /items/new - 品目登録画面")
    class NewItem {

        @Test
        @DisplayName("品目登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/items/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"));
        }
    }

    @Nested
    @DisplayName("POST /items - 品目登録処理")
    class CreateItem {

        @Test
        @DisplayName("品目を登録できる")
        void shouldCreateItem() throws Exception {
            Item created = createTestItem("NEW-001", "新規品目", ItemCategory.PRODUCT);
            Mockito.when(itemUseCase.createItem(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .param("itemCode", "NEW-001")
                    .param("itemName", "新規品目")
                    .param("itemCategory", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/items"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .param("itemCode", "")
                    .param("itemName", "")
                    .param("itemCategory", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "itemCode", "itemName"));
        }

        @Test
        @DisplayName("重複エラー時は入力画面に戻る")
        void shouldReturnFormOnDuplicateError() throws Exception {
            Mockito.when(itemUseCase.createItem(ArgumentMatchers.any()))
                .thenThrow(new DuplicateItemException("EXIST-001"));

            mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .param("itemCode", "EXIST-001")
                    .param("itemName", "重複品目")
                    .param("itemCategory", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "itemCode"));
        }
    }

    @Nested
    @DisplayName("GET /items/{itemCode}/edit - 品目編集画面")
    class EditItem {

        @Test
        @DisplayName("品目編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品", ItemCategory.PRODUCT);
            Mockito.when(itemUseCase.getItem("PROD-001")).thenReturn(item);

            mockMvc.perform(MockMvcRequestBuilders.get("/items/PROD-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("items/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attribute("itemCode", "PROD-001"));
        }
    }

    @Nested
    @DisplayName("POST /items/{itemCode} - 品目更新処理")
    class UpdateItem {

        @Test
        @DisplayName("品目を更新できる")
        void shouldUpdateItem() throws Exception {
            Item updated = createTestItem("PROD-001", "更新後品名", ItemCategory.PRODUCT);
            Mockito.when(itemUseCase.updateItem(ArgumentMatchers.eq("PROD-001"), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/items/PROD-001")
                    .param("itemCode", "PROD-001")
                    .param("itemName", "更新後品名")
                    .param("itemCategory", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/items/PROD-001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /items/{itemCode}/delete - 品目削除処理")
    class DeleteItem {

        @Test
        @DisplayName("品目を削除できる")
        void shouldDeleteItem() throws Exception {
            Mockito.doNothing().when(itemUseCase).deleteItem("PROD-001");

            mockMvc.perform(MockMvcRequestBuilders.post("/items/PROD-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/items"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("存在しない品目削除時はエラーメッセージ")
        void shouldShowErrorWhenNotFound() throws Exception {
            Mockito.doThrow(new ItemNotFoundException("NOT-EXIST"))
                .when(itemUseCase).deleteItem("NOT-EXIST");

            mockMvc.perform(MockMvcRequestBuilders.post("/items/NOT-EXIST/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/items"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
        }
    }

    private Item createTestItem(String code, String name, ItemCategory category) {
        return Item.builder()
            .id(1)
            .itemCode(code)
            .itemName(name)
            .itemCategory(category)
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .leadTime(10)
            .safetyStock(BigDecimal.TEN)
            .build();
    }
}
