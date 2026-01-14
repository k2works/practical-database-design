package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.AcceptanceUseCase;
import com.example.pms.application.port.in.InspectionUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Acceptance;
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
 * 検収画面コントローラーテスト.
 */
@WebMvcTest(AcceptanceWebController.class)
@DisplayName("検収画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class AcceptanceWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AcceptanceUseCase acceptanceUseCase;

    @MockitoBean
    private InspectionUseCase inspectionUseCase;

    @MockitoBean
    private PurchaseOrderUseCase purchaseOrderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private SupplierUseCase supplierUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(staffUseCase.getAllStaff()).thenReturn(Collections.emptyList());
        Mockito.when(purchaseOrderUseCase.getAllOrders()).thenReturn(Collections.emptyList());
        Mockito.when(supplierUseCase.getAllSuppliers()).thenReturn(Collections.emptyList());
        Mockito.when(inspectionUseCase.getAllInspections()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /acceptances - 検収一覧")
    class ListAcceptances {

        @Test
        @DisplayName("検収一覧画面を表示できる")
        void shouldDisplayAcceptancesList() throws Exception {
            Acceptance acceptance = createTestAcceptance("ACC-001", "PO-001");
            PageResult<Acceptance> pageResult = new PageResult<>(List.of(acceptance), 0, 20, 1);
            Mockito.when(acceptanceUseCase.getAcceptanceList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("acceptanceList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Acceptance acceptance = createTestAcceptance("ACC-001", "PO-001");
            PageResult<Acceptance> pageResult = new PageResult<>(List.of(acceptance), 0, 20, 1);
            Mockito.when(acceptanceUseCase.getAcceptanceList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("PO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances")
                    .param("keyword", "PO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "PO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Acceptance acceptance = createTestAcceptance("ACC-001", "PO-001");
            PageResult<Acceptance> pageResult = new PageResult<>(List.of(acceptance), 1, 10, 25);
            Mockito.when(acceptanceUseCase.getAcceptanceList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /acceptances/new - 検収登録画面")
    class NewAcceptance {

        @Test
        @DisplayName("検収登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /acceptances - 検収登録処理")
    class CreateAcceptance {

        @Test
        @DisplayName("検収を登録できる")
        void shouldCreateAcceptance() throws Exception {
            Acceptance created = createTestAcceptance("ACC-001", "PO-001");
            Mockito.when(acceptanceUseCase.createAcceptance(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/acceptances")
                    .param("inspectionNumber", "INS-001")
                    .param("purchaseOrderNumber", "PO-001")
                    .param("lineNumber", "1")
                    .param("acceptanceDate", "2024-01-20")
                    .param("supplierCode", "SUP-001")
                    .param("itemCode", "ITEM-001")
                    .param("acceptedQuantity", "100")
                    .param("unitPrice", "1000")
                    .param("amount", "100000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/acceptances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/acceptances")
                    .param("purchaseOrderNumber", "")
                    .param("acceptedQuantity", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "purchaseOrderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /acceptances/{acceptanceNumber} - 検収詳細画面")
    class ShowAcceptance {

        @Test
        @DisplayName("検収詳細画面を表示できる")
        void shouldDisplayAcceptanceDetail() throws Exception {
            Acceptance acceptance = createTestAcceptance("ACC-001", "PO-001");
            Mockito.when(acceptanceUseCase.getAcceptance("ACC-001"))
                .thenReturn(Optional.of(acceptance));

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances/ACC-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("acceptance"));
        }

        @Test
        @DisplayName("検収が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(acceptanceUseCase.getAcceptance("ACC-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances/ACC-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/acceptances"));
        }
    }

    @Nested
    @DisplayName("GET /acceptances/{acceptanceNumber}/edit - 検収編集画面")
    class EditAcceptance {

        @Test
        @DisplayName("検収編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Acceptance acceptance = createTestAcceptance("ACC-001", "PO-001");
            Mockito.when(acceptanceUseCase.getAcceptance("ACC-001"))
                .thenReturn(Optional.of(acceptance));

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances/ACC-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("acceptances/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("検収が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(acceptanceUseCase.getAcceptance("ACC-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/acceptances/ACC-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/acceptances"));
        }
    }

    @Nested
    @DisplayName("POST /acceptances/{acceptanceNumber} - 検収更新処理")
    class UpdateAcceptance {

        @Test
        @DisplayName("検収を更新できる")
        void shouldUpdateAcceptance() throws Exception {
            Acceptance updated = createTestAcceptance("ACC-001", "PO-001");
            Mockito.when(acceptanceUseCase.updateAcceptance(
                    ArgumentMatchers.eq("ACC-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/acceptances/ACC-001")
                    .param("inspectionNumber", "INS-001")
                    .param("purchaseOrderNumber", "PO-001")
                    .param("lineNumber", "1")
                    .param("acceptanceDate", "2024-01-20")
                    .param("supplierCode", "SUP-001")
                    .param("itemCode", "ITEM-001")
                    .param("acceptedQuantity", "150")
                    .param("unitPrice", "1000")
                    .param("amount", "150000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/acceptances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /acceptances/{acceptanceNumber}/delete - 検収削除処理")
    class DeleteAcceptance {

        @Test
        @DisplayName("検収を削除できる")
        void shouldDeleteAcceptance() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/acceptances/ACC-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/acceptances"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(acceptanceUseCase).deleteAcceptance("ACC-001");
        }
    }

    private Acceptance createTestAcceptance(String acceptanceNumber, String purchaseOrderNumber) {
        return Acceptance.builder()
            .id(1)
            .acceptanceNumber(acceptanceNumber)
            .purchaseOrderNumber(purchaseOrderNumber)
            .lineNumber(1)
            .acceptanceDate(LocalDate.of(2024, 1, 20))
            .acceptorCode("EMP-001")
            .supplierCode("SUP-001")
            .itemCode("ITEM-001")
            .miscellaneousItemFlag(false)
            .acceptedQuantity(new BigDecimal("100"))
            .unitPrice(new BigDecimal("1000"))
            .amount(new BigDecimal("100000"))
            .taxAmount(new BigDecimal("10000"))
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
