package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.StockUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stock;
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
import java.util.List;
import java.util.Optional;

/**
 * 在庫照会画面コントローラーテスト.
 */
@WebMvcTest(StockWebController.class)
@DisplayName("在庫照会画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class StockWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockUseCase stockUseCase;

    @Nested
    @DisplayName("GET /stocks - 在庫一覧")
    class ListStocks {

        @Test
        @DisplayName("在庫一覧画面を表示できる")
        void shouldDisplayStockList() throws Exception {
            Stock stock = createTestStock("LOC-001", "ITEM-001");
            PageResult<Stock> pageResult = new PageResult<>(List.of(stock), 0, 20, 1);
            Mockito.when(stockUseCase.getStockList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/stocks"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("stocks/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("stockList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Stock stock = createTestStock("LOC-001", "ITEM-001");
            PageResult<Stock> pageResult = new PageResult<>(List.of(stock), 0, 20, 1);
            Mockito.when(stockUseCase.getStockList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("LOC-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/stocks")
                    .param("keyword", "LOC-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("stocks/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "LOC-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Stock stock = createTestStock("LOC-001", "ITEM-001");
            PageResult<Stock> pageResult = new PageResult<>(List.of(stock), 1, 10, 25);
            Mockito.when(stockUseCase.getStockList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/stocks")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /stocks/{id} - 在庫詳細画面")
    class ShowStock {

        @Test
        @DisplayName("在庫詳細画面を表示できる")
        void shouldDisplayStockDetail() throws Exception {
            Stock stock = createTestStock("LOC-001", "ITEM-001");
            Mockito.when(stockUseCase.getStock(1))
                .thenReturn(Optional.of(stock));

            mockMvc.perform(MockMvcRequestBuilders.get("/stocks/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("stocks/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("stock"));
        }

        @Test
        @DisplayName("在庫が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(stockUseCase.getStock(999))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/stocks/999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/stocks"));
        }
    }

    private Stock createTestStock(String locationCode, String itemCode) {
        return Stock.builder()
            .id(1)
            .locationCode(locationCode)
            .itemCode(itemCode)
            .stockQuantity(new BigDecimal("100.00"))
            .passedQuantity(new BigDecimal("90.00"))
            .defectiveQuantity(new BigDecimal("5.00"))
            .uninspectedQuantity(new BigDecimal("5.00"))
            .version(1)
            .build();
    }
}
