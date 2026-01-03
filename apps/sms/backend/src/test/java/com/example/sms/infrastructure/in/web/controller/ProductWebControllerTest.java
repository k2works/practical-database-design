package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.CustomerProductPriceUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductClassificationUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.ProductClassification;
import com.example.sms.domain.model.product.TaxCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * 商品マスタ画面コントローラーテスト.
 */
@WebMvcTest(ProductWebController.class)
@DisplayName("商品マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProductWebControllerTest {

    private static final String TEST_CLASSIFICATION_CODE = "CAT-WEB-TEST";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductUseCase productUseCase;

    @MockitoBean
    private ProductClassificationUseCase classificationUseCase;

    @MockitoBean
    private CustomerProductPriceUseCase customerProductPriceUseCase;

    @MockitoBean
    private PartnerUseCase partnerUseCase;

    @Nested
    @DisplayName("GET /products")
    class ListProducts {

        @Test
        @DisplayName("商品一覧画面を表示できる")
        void shouldDisplayProductList() throws Exception {
            Product product = createTestProduct("WEB-001", "Web商品");
            when(productUseCase.getAllProducts()).thenReturn(List.of(product));

            mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"));
        }

        @Test
        @DisplayName("カテゴリでフィルタできる")
        void shouldFilterByCategory() throws Exception {
            Product product = createTestProduct("WEB-002", "商品商品");
            when(productUseCase.getAllProducts()).thenReturn(List.of(product));

            mockMvc.perform(MockMvcRequestBuilders.get("/products")
                    .param("category", "PRODUCT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedCategory", ProductCategory.PRODUCT));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Product product = createTestProduct("WEB-003", "検索テスト商品");
            when(productUseCase.getAllProducts()).thenReturn(List.of(product));

            mockMvc.perform(MockMvcRequestBuilders.get("/products")
                    .param("keyword", "検索"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "検索"));
        }
    }

    @Nested
    @DisplayName("GET /products/{productCode}")
    class ShowProduct {

        @Test
        @DisplayName("商品詳細画面を表示できる")
        void shouldDisplayProductDetail() throws Exception {
            Product product = createTestProduct("WEB-004", "詳細商品");
            when(productUseCase.getProductByCode("WEB-004")).thenReturn(product);
            when(customerProductPriceUseCase.getPricesByProduct("WEB-004"))
                .thenReturn(Collections.emptyList());

            mockMvc.perform(MockMvcRequestBuilders.get("/products/WEB-004"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("product"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("customerPrices"));
        }
    }

    @Nested
    @DisplayName("GET /products/new")
    class NewProductForm {

        @Test
        @DisplayName("商品登録フォームを表示できる")
        void shouldDisplayNewProductForm() throws Exception {
            when(classificationUseCase.getAllClassifications())
                .thenReturn(List.of(createTestClassification()));

            mockMvc.perform(MockMvcRequestBuilders.get("/products/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("taxCategories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classifications"));
        }
    }

    @Nested
    @DisplayName("POST /products")
    class CreateProduct {

        @Test
        @DisplayName("商品を登録できる")
        void shouldCreateProduct() throws Exception {
            Product created = createTestProduct("NEW-WEB-001", "新規商品");
            when(productUseCase.createProduct(any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/products")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("productCode", "NEW-WEB-001")
                    .param("productFullName", "新規商品 フルネーム")
                    .param("productName", "新規商品")
                    .param("productNameKana", "シンキショウヒン")
                    .param("productCategory", "PRODUCT")
                    .param("sellingPrice", "5000")
                    .param("purchasePrice", "3000")
                    .param("taxCategory", "EXCLUSIVE")
                    .param("classificationCode", TEST_CLASSIFICATION_CODE))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            when(classificationUseCase.getAllClassifications())
                .thenReturn(List.of(createTestClassification()));

            mockMvc.perform(MockMvcRequestBuilders.post("/products")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("productCode", "")
                    .param("productName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /products/{productCode}/edit")
    class EditProductForm {

        @Test
        @DisplayName("商品編集フォームを表示できる")
        void shouldDisplayEditProductForm() throws Exception {
            Product product = createTestProduct("WEB-EDIT-001", "編集商品");
            when(productUseCase.getProductByCode("WEB-EDIT-001")).thenReturn(product);
            when(classificationUseCase.getAllClassifications())
                .thenReturn(List.of(createTestClassification()));

            mockMvc.perform(MockMvcRequestBuilders.get("/products/WEB-EDIT-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("taxCategories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("classifications"));
        }
    }

    @Nested
    @DisplayName("POST /products/{productCode}")
    class UpdateProduct {

        @Test
        @DisplayName("商品を更新できる")
        void shouldUpdateProduct() throws Exception {
            Product updated = createTestProduct("WEB-UPD-001", "更新後商品");
            when(productUseCase.updateProduct(anyString(), any())).thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/products/WEB-UPD-001")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("productCode", "WEB-UPD-001")
                    .param("productFullName", "更新後商品 フルネーム")
                    .param("productName", "更新後商品")
                    .param("productNameKana", "コウシンゴショウヒン")
                    .param("productCategory", "PRODUCT")
                    .param("sellingPrice", "6000")
                    .param("purchasePrice", "4000")
                    .param("taxCategory", "EXCLUSIVE")
                    .param("classificationCode", TEST_CLASSIFICATION_CODE))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products/WEB-UPD-001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /products/{productCode}/delete")
    class DeleteProduct {

        @Test
        @DisplayName("商品を削除できる")
        void shouldDeleteProduct() throws Exception {
            doNothing().when(productUseCase).deleteProduct("WEB-DEL-001");

            mockMvc.perform(MockMvcRequestBuilders.post("/products/WEB-DEL-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private Product createTestProduct(String productCode, String productName) {
        return Product.builder()
            .productCode(productCode)
            .productFullName(productName + " フルネーム")
            .productName(productName)
            .productNameKana("テストショウヒン")
            .productCategory(ProductCategory.PRODUCT)
            .modelNumber("MODEL-001")
            .sellingPrice(new BigDecimal("5000"))
            .purchasePrice(new BigDecimal("3000"))
            .taxCategory(TaxCategory.EXCLUSIVE)
            .classificationCode(TEST_CLASSIFICATION_CODE)
            .isMiscellaneous(false)
            .isInventoryManaged(true)
            .isInventoryAllocated(true)
            .build();
    }

    private ProductClassification createTestClassification() {
        return ProductClassification.builder()
            .classificationCode(TEST_CLASSIFICATION_CODE)
            .classificationName("テスト分類")
            .classificationPath("/" + TEST_CLASSIFICATION_CODE)
            .hierarchyLevel(1)
            .isLeaf(true)
            .build();
    }
}
