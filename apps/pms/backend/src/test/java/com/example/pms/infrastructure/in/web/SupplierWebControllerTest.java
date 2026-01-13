package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
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
import java.util.List;

/**
 * 取引先マスタ画面コントローラーテスト.
 */
@WebMvcTest(SupplierWebController.class)
@DisplayName("取引先マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class SupplierWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierUseCase supplierUseCase;

    @Nested
    @DisplayName("GET /suppliers - 取引先一覧")
    class ListSuppliers {

        @Test
        @DisplayName("取引先一覧画面を表示できる")
        void shouldDisplaySupplierList() throws Exception {
            Supplier supplier = createTestSupplier("SUP-001", "テスト取引先");
            PageResult<Supplier> pageResult = new PageResult<>(List.of(supplier), 0, 20, 1);
            Mockito.when(supplierUseCase.getSuppliers(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/suppliers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("suppliers/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("suppliers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Supplier supplier = createTestSupplier("SUP-001", "テスト取引先");
            PageResult<Supplier> pageResult = new PageResult<>(List.of(supplier), 0, 20, 1);
            Mockito.when(supplierUseCase.getSuppliers(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("テスト")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/suppliers")
                    .param("keyword", "テスト"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("suppliers/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "テスト"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Supplier supplier = createTestSupplier("SUP-001", "テスト取引先");
            PageResult<Supplier> pageResult = new PageResult<>(List.of(supplier), 1, 10, 25);
            Mockito.when(supplierUseCase.getSuppliers(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/suppliers")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /suppliers/new - 取引先登録画面")
    class NewSupplier {

        @Test
        @DisplayName("取引先登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/suppliers/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("suppliers/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("supplierTypes"));
        }
    }

    @Nested
    @DisplayName("POST /suppliers - 取引先登録処理")
    class CreateSupplier {

        @Test
        @DisplayName("取引先を登録できる")
        void shouldCreateSupplier() throws Exception {
            Supplier created = createTestSupplier("NEW-001", "新規取引先");
            Mockito.when(supplierUseCase.createSupplier(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/suppliers")
                    .param("supplierCode", "NEW-001")
                    .param("supplierName", "新規取引先")
                    .param("supplierType", "VENDOR")
                    .param("effectiveFrom", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/suppliers"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/suppliers")
                    .param("supplierCode", "")
                    .param("supplierName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("suppliers/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "supplierCode", "supplierName"));
        }
    }

    private Supplier createTestSupplier(String code, String name) {
        return Supplier.builder()
            .supplierCode(code)
            .supplierName(name)
            .supplierType(SupplierType.VENDOR)
            .effectiveFrom(LocalDate.of(2024, 1, 1))
            .effectiveTo(LocalDate.of(9999, 12, 31))
            .build();
    }
}
