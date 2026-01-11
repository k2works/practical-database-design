package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 品目リポジトリテスト.
 */
@DisplayName("品目リポジトリ")
class ItemRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    private Item createItem(String code, String name, ItemCategory category) {
        return Item.builder()
                .itemCode(code)
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName(name)
                .itemCategory(category)
                .leadTime(5)
                .yieldRate(new BigDecimal("98.00"))
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("品目を登録できる")
        void canRegisterItem() {
            // Arrange
            Item item = createItem("TEST001", "テスト製品", ItemCategory.PRODUCT);

            // Act
            itemRepository.save(item);

            // Assert
            Optional<Item> found = itemRepository.findByItemCode("TEST001");
            assertThat(found).isPresent();
            assertThat(found.get().getItemName()).isEqualTo("テスト製品");
            assertThat(found.get().getItemCategory()).isEqualTo(ItemCategory.PRODUCT);
        }

        @Test
        @DisplayName("各品目区分を登録できる")
        void canRegisterAllItemCategories() {
            // Arrange & Act & Assert
            for (ItemCategory category : ItemCategory.values()) {
                Item item = Item.builder()
                        .itemCode("CAT_" + category.name())
                        .effectiveFrom(LocalDate.of(2024, 1, 1))
                        .itemName("テスト " + category.getDisplayName())
                        .itemCategory(category)
                        .build();
                itemRepository.save(item);

                Optional<Item> found = itemRepository.findByItemCode("CAT_" + category.name());
                assertThat(found).isPresent();
                assertThat(found.get().getItemCategory()).isEqualTo(category);
                assertThat(found.get().getItemCategory().getDisplayName())
                        .isEqualTo(category.getDisplayName());
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            itemRepository.save(createItem("PROD001", "製品A", ItemCategory.PRODUCT));
            itemRepository.save(createItem("PART001", "部品A", ItemCategory.PART));
            itemRepository.save(createItem("PART002", "部品B", ItemCategory.PART));
            itemRepository.save(createItem("MAT001", "材料A", ItemCategory.MATERIAL));
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            // Act
            Optional<Item> found = itemRepository.findByItemCode("PROD001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemName()).isEqualTo("製品A");
        }

        @Test
        @DisplayName("存在しないコードで検索すると空を返す")
        void returnsEmptyForNonExistentCode() {
            // Act
            Optional<Item> found = itemRepository.findByItemCode("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Item> items = itemRepository.findAll();

            // Assert
            assertThat(items).hasSize(4);
        }

        @Test
        @DisplayName("品目コードと日付で検索できる")
        void canFindByItemCodeAndDate() {
            // Arrange
            itemRepository.deleteAll();
            itemRepository.save(Item.builder()
                    .itemCode("TEST001")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .itemName("テスト製品 v1")
                    .itemCategory(ItemCategory.PRODUCT)
                    .build());
            itemRepository.save(Item.builder()
                    .itemCode("TEST001")
                    .effectiveFrom(LocalDate.of(2024, 6, 1))
                    .itemName("テスト製品 v2")
                    .itemCategory(ItemCategory.PRODUCT)
                    .build());

            // Act
            Optional<Item> foundV1 = itemRepository.findByItemCodeAndDate("TEST001", LocalDate.of(2024, 3, 1));
            Optional<Item> foundV2 = itemRepository.findByItemCodeAndDate("TEST001", LocalDate.of(2024, 7, 1));

            // Assert
            assertThat(foundV1).isPresent();
            assertThat(foundV1.get().getItemName()).isEqualTo("テスト製品 v1");
            assertThat(foundV2).isPresent();
            assertThat(foundV2.get().getItemName()).isEqualTo("テスト製品 v2");
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("品目を更新できる")
        void canUpdateItem() {
            // Arrange
            Item item = createItem("TEST001", "テスト製品", ItemCategory.PRODUCT);
            itemRepository.save(item);

            // Act
            Optional<Item> saved = itemRepository.findByItemCode("TEST001");
            assertThat(saved).isPresent();
            Item toUpdate = saved.get();
            toUpdate.setItemName("更新後の製品名");
            toUpdate.setLeadTime(10);
            itemRepository.update(toUpdate);

            // Assert
            Optional<Item> updated = itemRepository.findByItemCode("TEST001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getItemName()).isEqualTo("更新後の製品名");
            assertThat(updated.get().getLeadTime()).isEqualTo(10);
        }
    }
}
