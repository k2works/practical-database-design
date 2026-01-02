package com.example.sms.infrastructure.in.rest;

import com.example.sms.application.port.out.ProductClassificationRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.ProductClassification;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

/**
 * 商品マスタ API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("商品マスタ API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProductControllerTest extends BaseIntegrationTest {

    private static final String TEST_CLASSIFICATION_CODE = "CAT-TEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductClassificationRepository productClassificationRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productClassificationRepository.deleteAll();
        // テスト用の商品分類を作成
        ProductClassification classification = ProductClassification.builder()
            .classificationCode(TEST_CLASSIFICATION_CODE)
            .classificationName("テスト分類")
            .hierarchyLevel(1)
            .classificationPath("/CAT-TEST")
            .isLeaf(true)
            .build();
        productClassificationRepository.save(classification);
    }

    @Nested
    @DisplayName("GET /api/v1/products")
    class GetAllProducts {

        @Test
        @DisplayName("商品一覧を取得できる")
        void shouldGetAllProducts() throws Exception {
            // Given
            Product product = createTestProduct("TEST-001", "テスト商品");
            productRepository.save(product);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").value("TEST-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("テスト商品"));
        }

        @Test
        @DisplayName("商品が存在しない場合は空配列を返す")
        void shouldReturnEmptyArrayWhenNoProducts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/{productCode}")
    class GetProduct {

        @Test
        @DisplayName("商品コードで商品を取得できる")
        void shouldGetProductByCode() throws Exception {
            // Given
            Product product = createTestProduct("TEST-001", "テスト商品");
            productRepository.save(product);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/TEST-001")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCode").value("TEST-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("テスト商品"));
        }

        @Test
        @DisplayName("存在しない商品コードの場合は404を返す")
        void shouldReturn404WhenProductNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("商品が見つかりません: UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/products")
    class CreateProduct {

        @Test
        @DisplayName("商品を登録できる")
        void shouldCreateProduct() throws Exception {
            var request = """
                {
                    "productCode": "NEW-001",
                    "productFullName": "新規テスト商品 フルネーム",
                    "productName": "新規テスト商品",
                    "productNameKana": "シンキテストショウヒン",
                    "productCategory": "PRODUCT",
                    "modelNumber": "MODEL-001",
                    "sellingPrice": 5000,
                    "purchasePrice": 3000,
                    "taxCategory": "EXCLUSIVE",
                    "classificationCode": "CAT-TEST"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCode").value("NEW-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("新規テスト商品"));
        }

        @Test
        @DisplayName("商品コードが重複している場合は409を返す")
        void shouldReturn409WhenProductCodeDuplicate() throws Exception {
            // Given
            Product existing = createTestProduct("DUP-001", "既存商品");
            productRepository.save(existing);

            var request = """
                {
                    "productCode": "DUP-001",
                    "productFullName": "重複商品",
                    "productName": "重複商品",
                    "productCategory": "PRODUCT",
                    "sellingPrice": 1000,
                    "purchasePrice": 500,
                    "taxCategory": "EXCLUSIVE"
                }
                """;

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("CONFLICT"));
        }

        @Test
        @DisplayName("必須項目が欠けている場合は400を返す")
        void shouldReturn400WhenRequiredFieldMissing() throws Exception {
            var request = """
                {
                    "productName": "商品名のみ"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/products/{productCode}")
    class UpdateProduct {

        @Test
        @DisplayName("商品を更新できる")
        void shouldUpdateProduct() throws Exception {
            // Given
            Product product = createTestProduct("UPD-001", "更新前商品");
            productRepository.save(product);

            var request = """
                {
                    "productFullName": "更新後商品 フルネーム",
                    "productName": "更新後商品",
                    "productNameKana": "コウシンゴショウヒン",
                    "productCategory": "PRODUCT",
                    "sellingPrice": 6000,
                    "purchasePrice": 4000,
                    "taxCategory": "EXCLUSIVE"
                }
                """;

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/UPD-001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCode").value("UPD-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("更新後商品"));
        }

        @Test
        @DisplayName("存在しない商品コードの場合は404を返す")
        void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
            var request = """
                {
                    "productName": "更新商品",
                    "productCategory": "PRODUCT",
                    "sellingPrice": 1000,
                    "purchasePrice": 500,
                    "taxCategory": "EXCLUSIVE"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/products/UNKNOWN")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/products/{productCode}")
    class DeleteProduct {

        @Test
        @DisplayName("商品を削除できる")
        void shouldDeleteProduct() throws Exception {
            // Given
            Product product = createTestProduct("DEL-001", "削除対象商品");
            productRepository.save(product);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/DEL-001"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

            // Verify deletion
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/DEL-001"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("存在しない商品コードの場合は404を返す")
        void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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
}
