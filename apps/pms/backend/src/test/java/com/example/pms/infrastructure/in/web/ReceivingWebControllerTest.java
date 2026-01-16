package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.ReceivingUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;
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
 * 入荷受入画面コントローラーテスト.
 */
@WebMvcTest(ReceivingWebController.class)
@DisplayName("入荷受入画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ReceivingWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceivingUseCase receivingUseCase;

    @MockitoBean
    private PurchaseOrderUseCase purchaseOrderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(staffUseCase.getAllStaff()).thenReturn(Collections.emptyList());
        Mockito.when(purchaseOrderUseCase.getAllOrders()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /receivings - 入荷受入一覧")
    class ListReceivings {

        @Test
        @DisplayName("入荷受入一覧画面を表示できる")
        void shouldDisplayReceivingsList() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            PageResult<Receiving> pageResult = new PageResult<>(List.of(receiving), 0, 20, 1);
            Mockito.when(receivingUseCase.getReceivingList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("receivingList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("入荷種別でフィルタできる")
        void shouldFilterByReceivingType() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            PageResult<Receiving> pageResult = new PageResult<>(List.of(receiving), 0, 20, 1);
            Mockito.when(receivingUseCase.getReceivingList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq(ReceivingType.NORMAL),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings")
                    .param("receivingType", "NORMAL"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("receivingType", "NORMAL"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            PageResult<Receiving> pageResult = new PageResult<>(List.of(receiving), 0, 20, 1);
            Mockito.when(receivingUseCase.getReceivingList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("PO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings")
                    .param("keyword", "PO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "PO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            PageResult<Receiving> pageResult = new PageResult<>(List.of(receiving), 1, 10, 25);
            Mockito.when(receivingUseCase.getReceivingList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /receivings/new - 入荷受入登録画面")
    class NewReceiving {

        @Test
        @DisplayName("入荷受入登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/receivings/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /receivings - 入荷受入登録処理")
    class CreateReceiving {

        @Test
        @DisplayName("入荷受入を登録できる")
        void shouldCreateReceiving() throws Exception {
            Receiving created = createTestReceiving("RCV-001", "PO-001");
            Mockito.when(receivingUseCase.createReceiving(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/receivings")
                    .param("purchaseOrderNumber", "PO-001")
                    .param("lineNumber", "1")
                    .param("receivingDate", "2024-01-15")
                    .param("receivingType", "NORMAL")
                    .param("itemCode", "ITEM-001")
                    .param("receivingQuantity", "100"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/receivings"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/receivings")
                    .param("purchaseOrderNumber", "")
                    .param("receivingQuantity", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "purchaseOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /receivings/{receivingNumber} - 入荷受入詳細画面")
    class ShowReceiving {

        @Test
        @DisplayName("入荷受入詳細画面を表示できる")
        void shouldDisplayReceivingDetail() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            Mockito.when(receivingUseCase.getReceivingWithInspections("RCV-001"))
                .thenReturn(Optional.of(receiving));

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings/RCV-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("receiving"));
        }

        @Test
        @DisplayName("入荷受入が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(receivingUseCase.getReceivingWithInspections("RCV-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings/RCV-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/receivings"));
        }
    }

    @Nested
    @DisplayName("GET /receivings/{receivingNumber}/edit - 入荷受入編集画面")
    class EditReceiving {

        @Test
        @DisplayName("入荷受入編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Receiving receiving = createTestReceiving("RCV-001", "PO-001");
            Mockito.when(receivingUseCase.getReceiving("RCV-001"))
                .thenReturn(Optional.of(receiving));

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings/RCV-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("receivings/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("入荷受入が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(receivingUseCase.getReceiving("RCV-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/receivings/RCV-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/receivings"));
        }
    }

    @Nested
    @DisplayName("POST /receivings/{receivingNumber} - 入荷受入更新処理")
    class UpdateReceiving {

        @Test
        @DisplayName("入荷受入を更新できる")
        void shouldUpdateReceiving() throws Exception {
            Receiving updated = createTestReceiving("RCV-001", "PO-001");
            Mockito.when(receivingUseCase.updateReceiving(
                    ArgumentMatchers.eq("RCV-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/receivings/RCV-001")
                    .param("purchaseOrderNumber", "PO-001")
                    .param("lineNumber", "1")
                    .param("receivingDate", "2024-01-15")
                    .param("receivingType", "NORMAL")
                    .param("itemCode", "ITEM-001")
                    .param("receivingQuantity", "150"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/receivings"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /receivings/{receivingNumber}/delete - 入荷受入削除処理")
    class DeleteReceiving {

        @Test
        @DisplayName("入荷受入を削除できる")
        void shouldDeleteReceiving() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/receivings/RCV-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/receivings"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(receivingUseCase).deleteReceiving("RCV-001");
        }
    }

    private Receiving createTestReceiving(String receivingNumber, String purchaseOrderNumber) {
        return Receiving.builder()
            .id(1)
            .receivingNumber(receivingNumber)
            .purchaseOrderNumber(purchaseOrderNumber)
            .lineNumber(1)
            .receivingDate(LocalDate.of(2024, 1, 15))
            .receiverCode("EMP-001")
            .receivingType(ReceivingType.NORMAL)
            .itemCode("ITEM-001")
            .miscellaneousItemFlag(false)
            .receivingQuantity(new BigDecimal("100"))
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
