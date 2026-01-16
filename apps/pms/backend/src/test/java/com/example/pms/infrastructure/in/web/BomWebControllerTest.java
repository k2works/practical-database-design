package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.BomUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;
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
 * BOM マスタ画面コントローラーテスト.
 */
@WebMvcTest(BomWebController.class)
@DisplayName("BOM マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class BomWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BomUseCase bomUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @Nested
    @DisplayName("GET /bom - BOM 一覧")
    class ListBom {

        @Test
        @DisplayName("BOM 一覧画面を表示できる")
        void shouldDisplayBomList() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品");
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/bom"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"));
        }

        @Test
        @DisplayName("品目区分でフィルタできる")
        void shouldFilterByCategory() throws Exception {
            Item product = createTestItem("PROD-001", "製品A");
            PageResult<Item> pageResult = new PageResult<>(List.of(product), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq(ItemCategory.PRODUCT),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/bom")
                    .param("category", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedCategory", ItemCategory.PRODUCT));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品");
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 0, 20, 1);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("テスト")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/bom")
                    .param("keyword", "テスト"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "テスト"));
        }

        @Test
        @DisplayName("ページネーションパラメータが渡される")
        void shouldPassPaginationParams() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品");
            PageResult<Item> pageResult = new PageResult<>(List.of(item), 2, 50, 150);
            Mockito.when(itemUseCase.getItems(
                    ArgumentMatchers.eq(2),
                    ArgumentMatchers.eq(50),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/bom")
                    .param("page", "2")
                    .param("size", "50"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 50));
        }
    }

    @Nested
    @DisplayName("GET /bom/{parentItemCode} - 構成表")
    class ShowBom {

        @Test
        @DisplayName("構成表を表示できる")
        void shouldDisplayBomStructure() throws Exception {
            Item parentItem = createTestItem("PROD-001", "親製品");
            Bom bom = createTestBom("PROD-001", "PART-001");

            Mockito.when(itemUseCase.getItem("PROD-001")).thenReturn(parentItem);
            Mockito.when(bomUseCase.getBomByParentItem("PROD-001")).thenReturn(List.of(bom));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/PROD-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("parentItem"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bomList"));
        }

        @Test
        @DisplayName("存在しない品目は404エラー")
        void shouldReturn404WhenNotFound() throws Exception {
            Mockito.when(itemUseCase.getItem("NOT-EXIST"))
                .thenThrow(new ItemNotFoundException("NOT-EXIST"));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/NOT-EXIST"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /bom/{itemCode}/explode - BOM 展開")
    class ExplodeBom {

        @Test
        @DisplayName("BOM 展開結果を表示できる")
        void shouldDisplayExplodedBom() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品");
            BomExplosion explosion = createTestExplosion("PROD-001", "PART-001", 1);

            Mockito.when(itemUseCase.getItem("PROD-001")).thenReturn(item);
            Mockito.when(bomUseCase.explodeBom("PROD-001", BigDecimal.ONE))
                .thenReturn(List.of(explosion));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/PROD-001/explode"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/explode"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("item"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("quantity"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("explosionList"));
        }

        @Test
        @DisplayName("数量を指定して展開できる")
        void shouldExplodeWithQuantity() throws Exception {
            Item item = createTestItem("PROD-001", "テスト製品");
            BomExplosion explosion = createTestExplosion("PROD-001", "PART-001", 1);
            BigDecimal quantity = BigDecimal.valueOf(10);

            Mockito.when(itemUseCase.getItem("PROD-001")).thenReturn(item);
            Mockito.when(bomUseCase.explodeBom("PROD-001", quantity))
                .thenReturn(List.of(explosion));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/PROD-001/explode")
                    .param("quantity", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/explode"))
                .andExpect(MockMvcResultMatchers.model().attribute("quantity", quantity));
        }
    }

    @Nested
    @DisplayName("GET /bom/{childItemCode}/where-used - 使用先照会")
    class WhereUsed {

        @Test
        @DisplayName("使用先照会結果を表示できる")
        void shouldDisplayWhereUsed() throws Exception {
            Item childItem = createTestItem("PART-001", "部品A");
            Bom whereUsed = createTestBom("PROD-001", "PART-001");

            Mockito.when(itemUseCase.getItem("PART-001")).thenReturn(childItem);
            Mockito.when(bomUseCase.whereUsed("PART-001")).thenReturn(List.of(whereUsed));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/PART-001/where-used"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bom/where-used"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("childItem"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("whereUsedList"));
        }

        @Test
        @DisplayName("存在しない品目は404エラー")
        void shouldReturn404WhenNotFound() throws Exception {
            Mockito.when(itemUseCase.getItem("NOT-EXIST"))
                .thenThrow(new ItemNotFoundException("NOT-EXIST"));

            mockMvc.perform(MockMvcRequestBuilders.get("/bom/NOT-EXIST/where-used"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    private Item createTestItem(String code, String name) {
        return Item.builder()
            .id(1)
            .itemCode(code)
            .itemName(name)
            .itemCategory(ItemCategory.PRODUCT)
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .leadTime(10)
            .safetyStock(BigDecimal.TEN)
            .build();
    }

    private Bom createTestBom(String parentCode, String childCode) {
        return Bom.builder()
            .parentItemCode(parentCode)
            .childItemCode(childCode)
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .baseQuantity(BigDecimal.ONE)
            .requiredQuantity(BigDecimal.valueOf(2))
            .defectRate(BigDecimal.ZERO)
            .sequence(1)
            .build();
    }

    private BomExplosion createTestExplosion(String parentCode, String childCode, int level) {
        return BomExplosion.builder()
            .parentItemCode(parentCode)
            .childItemCode(childCode)
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .baseQuantity(BigDecimal.ONE)
            .requiredQuantity(BigDecimal.valueOf(2))
            .defectRate(BigDecimal.ZERO)
            .sequence(1)
            .level(level)
            .totalQuantity(BigDecimal.valueOf(2))
            .build();
    }
}
