package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 取引先マスタリポジトリテスト.
 */
@DisplayName("取引先マスタリポジトリ")
class SupplierRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();
    }

    private Supplier createSupplier(String code, String name, SupplierType type) {
        return Supplier.builder()
                .supplierCode(code)
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .supplierName(name)
                .supplierType(type)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("取引先を登録できる")
        void canRegisterSupplier() {
            // Arrange
            Supplier supplier = createSupplier("SUP001", "テスト仕入先", SupplierType.VENDOR);

            // Act
            supplierRepository.save(supplier);

            // Assert
            Optional<Supplier> found = supplierRepository.findBySupplierCode("SUP001");
            assertThat(found).isPresent();
            assertThat(found.get().getSupplierName()).isEqualTo("テスト仕入先");
            assertThat(found.get().getSupplierType()).isEqualTo(SupplierType.VENDOR);
        }

        @Test
        @DisplayName("各取引先区分を登録できる")
        void canRegisterAllSupplierTypes() {
            int idx = 1;
            for (SupplierType type : SupplierType.values()) {
                String code = String.format("SUP%03d", idx++);
                Supplier supplier = createSupplier(code, "テスト " + type.getDisplayName(), type);
                supplierRepository.save(supplier);

                Optional<Supplier> found = supplierRepository.findBySupplierCode(code);
                assertThat(found).isPresent();
                assertThat(found.get().getSupplierType()).isEqualTo(type);
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            supplierRepository.save(createSupplier("SUP001", "仕入先A", SupplierType.VENDOR));
            supplierRepository.save(createSupplier("SUP002", "外注先B", SupplierType.SUBCONTRACTOR));
            supplierRepository.save(createSupplier("SUP003", "得意先C", SupplierType.CUSTOMER));
        }

        @Test
        @DisplayName("取引先コードで検索できる")
        void canFindBySupplierCode() {
            Optional<Supplier> found = supplierRepository.findBySupplierCode("SUP002");

            assertThat(found).isPresent();
            assertThat(found.get().getSupplierName()).isEqualTo("外注先B");
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<Supplier> found = supplierRepository.findAll();

            assertThat(found).hasSize(3);
        }

        @Test
        @DisplayName("世代管理対応で日付検索できる")
        void canFindBySupplierCodeAndDate() {
            // Arrange
            supplierRepository.deleteAll();
            supplierRepository.save(Supplier.builder()
                    .supplierCode("SUP001")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .effectiveTo(LocalDate.of(2024, 6, 1))
                    .supplierName("仕入先A v1")
                    .supplierType(SupplierType.VENDOR)
                    .build());
            supplierRepository.save(Supplier.builder()
                    .supplierCode("SUP001")
                    .effectiveFrom(LocalDate.of(2024, 6, 1))
                    .supplierName("仕入先A v2")
                    .supplierType(SupplierType.VENDOR)
                    .build());

            // Act
            Optional<Supplier> v1 = supplierRepository.findBySupplierCodeAndDate("SUP001", LocalDate.of(2024, 3, 1));
            Optional<Supplier> v2 = supplierRepository.findBySupplierCodeAndDate("SUP001", LocalDate.of(2024, 7, 1));

            // Assert
            assertThat(v1).isPresent();
            assertThat(v1.get().getSupplierName()).isEqualTo("仕入先A v1");
            assertThat(v2).isPresent();
            assertThat(v2.get().getSupplierName()).isEqualTo("仕入先A v2");
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("取引先を更新できる")
        void canUpdateSupplier() {
            // Arrange
            Supplier supplier = Supplier.builder()
                    .supplierCode("SUP001")
                    .effectiveFrom(LocalDate.of(2024, 1, 1))
                    .supplierName("仕入先A")
                    .supplierType(SupplierType.VENDOR)
                    .build();
            supplierRepository.save(supplier);

            // Act
            Optional<Supplier> saved = supplierRepository.findBySupplierCode("SUP001");
            assertThat(saved).isPresent();
            Supplier toUpdate = saved.get();
            toUpdate.setSupplierName("仕入先A（更新後）");
            toUpdate.setPhoneNumber("03-1234-5678");
            supplierRepository.update(toUpdate);

            // Assert
            Optional<Supplier> updated = supplierRepository.findBySupplierCode("SUP001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getSupplierName()).isEqualTo("仕入先A（更新後）");
            assertThat(updated.get().getPhoneNumber()).isEqualTo("03-1234-5678");
        }
    }
}
