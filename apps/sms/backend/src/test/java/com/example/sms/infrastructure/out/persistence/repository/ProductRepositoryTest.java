package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商品リポジトリテスト.
 */
@DisplayName("商品リポジトリ")
class ProductRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("商品を登録できる")
        void canRegisterProduct() {
            // Arrange
            var product = Product.builder()
                    .productCode("PROD001")
                    .productName("テスト商品")
                    .productCategory(ProductCategory.PRODUCT)
                    .sellingPrice(new BigDecimal("1000"))
                    .purchasePrice(new BigDecimal("700"))
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .build();

            // Act
            productRepository.save(product);

            // Assert
            var result = productRepository.findByCode("PROD001");
            assertThat(result).isPresent();
            assertThat(result.get().getProductName()).isEqualTo("テスト商品");
            assertThat(result.get().getSellingPrice()).isEqualByComparingTo(new BigDecimal("1000"));
        }

        @Test
        @DisplayName("全ての商品区分を登録できる")
        void canRegisterAllCategories() {
            var categories = ProductCategory.values();

            for (int i = 0; i < categories.length; i++) {
                var product = Product.builder()
                        .productCode("CAT-" + String.format("%03d", i))
                        .productName("商品" + categories[i].getDisplayName())
                        .productCategory(categories[i])
                        .taxCategory(TaxCategory.EXCLUSIVE)
                        .build();

                productRepository.save(product);

                var result = productRepository.findByCode(product.getProductCode());
                assertThat(result).isPresent();
                assertThat(result.get().getProductCategory()).isEqualTo(categories[i]);
            }
        }
    }

    @Nested
    @DisplayName("税区分")
    class TaxCategories {

        @Test
        @DisplayName("外税商品を登録できる")
        void canRegisterExclusiveTax() {
            var product = createProduct("TAX001", "外税商品", TaxCategory.EXCLUSIVE);
            productRepository.save(product);

            var result = productRepository.findByCode("TAX001");
            assertThat(result.get().getTaxCategory()).isEqualTo(TaxCategory.EXCLUSIVE);
        }

        @Test
        @DisplayName("内税商品を登録できる")
        void canRegisterInclusiveTax() {
            var product = createProduct("TAX002", "内税商品", TaxCategory.INCLUSIVE);
            productRepository.save(product);

            var result = productRepository.findByCode("TAX002");
            assertThat(result.get().getTaxCategory()).isEqualTo(TaxCategory.INCLUSIVE);
        }

        @Test
        @DisplayName("非課税商品を登録できる")
        void canRegisterTaxFree() {
            var product = createProduct("TAX003", "非課税商品", TaxCategory.TAX_FREE);
            productRepository.save(product);

            var result = productRepository.findByCode("TAX003");
            assertThat(result.get().getTaxCategory()).isEqualTo(TaxCategory.TAX_FREE);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("商品区分で検索できる")
        void canFindByCategory() {
            // Arrange
            var product1 = createProduct("PROD001", "商品1", TaxCategory.EXCLUSIVE);
            product1.setProductCategory(ProductCategory.PRODUCT);
            productRepository.save(product1);

            var product2 = createProduct("PROD002", "サービス1", TaxCategory.EXCLUSIVE);
            product2.setProductCategory(ProductCategory.SERVICE);
            productRepository.save(product2);

            var product3 = createProduct("PROD003", "商品2", TaxCategory.EXCLUSIVE);
            product3.setProductCategory(ProductCategory.PRODUCT);
            productRepository.save(product3);

            // Act
            var products = productRepository.findByCategory(ProductCategory.PRODUCT);

            // Assert
            assertThat(products).hasSize(2);
            assertThat(products)
                    .extracting(Product::getProductName)
                    .containsExactlyInAnyOrder("商品1", "商品2");
        }
    }

    private Product createProduct(String code, String name, TaxCategory taxCategory) {
        return Product.builder()
                .productCode(code)
                .productName(name)
                .productCategory(ProductCategory.PRODUCT)
                .taxCategory(taxCategory)
                .build();
    }
}
