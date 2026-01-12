package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.application.port.out.UnitPriceRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
import com.example.pms.domain.model.unitprice.UnitPrice;
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

@DisplayName("単価マスタリポジトリ")
class UnitPriceRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private UnitPriceRepository unitPriceRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @BeforeEach
    void setUp() {
        unitPriceRepository.deleteAll();
        supplierRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private void setupMasterData() {
        itemRepository.save(Item.builder()
                .itemCode("PART001")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .itemName("部品A")
                .itemCategory(ItemCategory.PART)
                .build());
        supplierRepository.save(Supplier.builder()
                .supplierCode("SUP001")
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .supplierName("仕入先A")
                .supplierType(SupplierType.VENDOR)
                .build());
    }

    private UnitPrice createUnitPrice(String itemCode, String supplierCode, BigDecimal price) {
        return UnitPrice.builder()
                .itemCode(itemCode)
                .supplierCode(supplierCode)
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .price(price)
                .currencyCode("JPY")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("単価を登録できる")
        void canRegisterUnitPrice() {
            setupMasterData();
            UnitPrice unitPrice = createUnitPrice("PART001", "SUP001", new BigDecimal("1000.00"));
            unitPriceRepository.save(unitPrice);

            Optional<UnitPrice> found = unitPriceRepository.findByItemCodeAndSupplierCode("PART001", "SUP001");
            assertThat(found).isPresent();
            assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            setupMasterData();
            unitPriceRepository.save(createUnitPrice("PART001", "SUP001", new BigDecimal("1000.00")));
        }

        @Test
        @DisplayName("品目コードと仕入先コードで検索できる")
        void canFindByItemCodeAndSupplierCode() {
            Optional<UnitPrice> found = unitPriceRepository.findByItemCodeAndSupplierCode("PART001", "SUP001");
            assertThat(found).isPresent();
            assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
        }

        @Test
        @DisplayName("存在しない品目コードと仕入先コードで検索すると空を返す")
        void returnsEmptyForNonExistentItemCodeAndSupplierCode() {
            Optional<UnitPrice> found = unitPriceRepository.findByItemCodeAndSupplierCode("NOTEXIST", "NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            List<UnitPrice> found = unitPriceRepository.findByItemCode("PART001");
            assertThat(found).hasSize(1);
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<UnitPrice> all = unitPriceRepository.findAll();
            assertThat(all).hasSize(1);
        }

        @Test
        @DisplayName("世代管理対応で日付検索できる")
        void canFindByItemCodeAndSupplierCodeAndDate() {
            unitPriceRepository.deleteAll();
            unitPriceRepository.save(UnitPrice.builder()
                    .itemCode("PART001")
                    .supplierCode("SUP001")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(LocalDate.of(2024, 6, 1))
                    .price(new BigDecimal("1000.00"))
                    .currencyCode("JPY")
                    .build());
            unitPriceRepository.save(UnitPrice.builder()
                    .itemCode("PART001")
                    .supplierCode("SUP001")
                    .effectiveFrom(LocalDate.of(2024, 6, 1))
                    .price(new BigDecimal("1200.00"))
                    .currencyCode("JPY")
                    .build());

            Optional<UnitPrice> v1 = unitPriceRepository.findByItemCodeAndSupplierCodeAndDate(
                    "PART001", "SUP001", LocalDate.of(2024, 3, 1));
            Optional<UnitPrice> v2 = unitPriceRepository.findByItemCodeAndSupplierCodeAndDate(
                    "PART001", "SUP001", LocalDate.of(2024, 7, 1));

            assertThat(v1).isPresent();
            assertThat(v1.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
            assertThat(v2).isPresent();
            assertThat(v2.get().getPrice()).isEqualByComparingTo(new BigDecimal("1200.00"));
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("単価を更新できる")
        void canUpdateUnitPrice() {
            setupMasterData();
            unitPriceRepository.save(createUnitPrice("PART001", "SUP001", new BigDecimal("1000.00")));

            Optional<UnitPrice> saved = unitPriceRepository.findByItemCodeAndSupplierCode("PART001", "SUP001");
            assertThat(saved).isPresent();
            UnitPrice toUpdate = saved.get();
            toUpdate.setPrice(new BigDecimal("1100.00"));
            unitPriceRepository.update(toUpdate);

            Optional<UnitPrice> updated = unitPriceRepository.findByItemCodeAndSupplierCode("PART001", "SUP001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getPrice()).isEqualByComparingTo(new BigDecimal("1100.00"));
        }
    }
}
