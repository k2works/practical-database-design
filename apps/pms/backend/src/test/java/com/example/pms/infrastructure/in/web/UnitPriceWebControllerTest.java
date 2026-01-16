package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.application.port.in.UnitPriceUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unitprice.UnitPrice;
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
 * 単価マスタ画面コントローラーテスト.
 */
@WebMvcTest(UnitPriceWebController.class)
@DisplayName("単価マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class UnitPriceWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitPriceUseCase unitPriceUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private SupplierUseCase supplierUseCase;

    @Nested
    @DisplayName("GET /prices - 単価一覧")
    class ListPrices {

        @Test
        @DisplayName("単価一覧画面を表示できる")
        void shouldDisplayPriceList() throws Exception {
            UnitPrice price = createTestPrice("ITEM-001", "SUP-001");
            PageResult<UnitPrice> pageResult = new PageResult<>(List.of(price), 0, 20, 1);
            Mockito.when(unitPriceUseCase.getUnitPrices(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/prices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("prices/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("prices"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void shouldSearchByItemCode() throws Exception {
            UnitPrice price = createTestPrice("ITEM-001", "SUP-001");
            PageResult<UnitPrice> pageResult = new PageResult<>(List.of(price), 0, 20, 1);
            Mockito.when(unitPriceUseCase.getUnitPrices(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("ITEM-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/prices")
                    .param("itemCode", "ITEM-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("prices/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("itemCode", "ITEM-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            UnitPrice price = createTestPrice("ITEM-001", "SUP-001");
            PageResult<UnitPrice> pageResult = new PageResult<>(List.of(price), 1, 10, 25);
            Mockito.when(unitPriceUseCase.getUnitPrices(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/prices")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /prices/new - 単価登録画面")
    class NewPrice {

        @Test
        @DisplayName("単価登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            Mockito.when(itemUseCase.getAllItems()).thenReturn(List.of());
            Mockito.when(supplierUseCase.getAllSuppliers()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/prices/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("prices/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("suppliers"));
        }
    }

    @Nested
    @DisplayName("POST /prices - 単価登録処理")
    class CreatePrice {

        @Test
        @DisplayName("単価を登録できる")
        void shouldCreatePrice() throws Exception {
            UnitPrice created = createTestPrice("ITEM-001", "SUP-001");
            Mockito.when(unitPriceUseCase.createUnitPrice(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/prices")
                    .param("itemCode", "ITEM-001")
                    .param("supplierCode", "SUP-001")
                    .param("effectiveFrom", "2024-01-01")
                    .param("price", "1000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/prices"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            Mockito.when(itemUseCase.getAllItems()).thenReturn(List.of());
            Mockito.when(supplierUseCase.getAllSuppliers()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.post("/prices")
                    .param("itemCode", "")
                    .param("supplierCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("prices/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "itemCode", "supplierCode"));
        }
    }

    private UnitPrice createTestPrice(String itemCode, String supplierCode) {
        return UnitPrice.builder()
            .itemCode(itemCode)
            .supplierCode(supplierCode)
            .effectiveFrom(LocalDate.of(2024, 1, 1))
            .effectiveTo(LocalDate.of(9999, 12, 31))
            .price(new BigDecimal("1000"))
            .currencyCode("JPY")
            .build();
    }
}
