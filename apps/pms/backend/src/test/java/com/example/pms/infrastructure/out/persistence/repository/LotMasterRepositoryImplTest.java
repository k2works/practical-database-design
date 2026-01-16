package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LotCompositionRepository;
import com.example.pms.application.port.out.LotMasterRepository;
import com.example.pms.domain.model.quality.LotComposition;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.domain.model.quality.LotType;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ロットマスタリポジトリテスト.
 */
@DisplayName("ロットマスタリポジトリ")
class LotMasterRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private LotMasterRepository lotMasterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lotMasterRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト材料', '材料')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト製品', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "倉庫マスタ" ("倉庫コード", "倉庫区分", "倉庫名")
            VALUES ('WH001', '製品倉庫', 'テスト倉庫')
            ON CONFLICT DO NOTHING
            """);
    }

    private LotMaster createLotMaster(String lotNumber, String itemCode, LotType lotType) {
        return LotMaster.builder()
                .lotNumber(lotNumber)
                .itemCode(itemCode)
                .lotType(lotType)
                .manufactureDate(LocalDate.of(2024, 1, 15))
                .expirationDate(LocalDate.of(2025, 1, 15))
                .quantity(new BigDecimal("100.00"))
                .warehouseCode("WH001")
                .remarks("テストロット")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {
        @Test
        @DisplayName("ロットを登録できる")
        void canRegisterLotMaster() {
            LotMaster lot = createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED);
            lotMasterRepository.save(lot);

            Optional<LotMaster> found = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getLotType()).isEqualTo(LotType.PURCHASED);
        }

        @Test
        @DisplayName("複数のロットを登録できる")
        void canRegisterMultipleLots() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));
            lotMasterRepository.save(createLotMaster("LOT-002", "ITEM002", LotType.MANUFACTURED));

            List<LotMaster> found = lotMasterRepository.findAll();
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {
        @BeforeEach
        void setUpData() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));
            lotMasterRepository.save(createLotMaster("LOT-002", "ITEM001", LotType.PURCHASED));
            lotMasterRepository.save(createLotMaster("LOT-003", "ITEM002", LotType.MANUFACTURED));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            Optional<LotMaster> result = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(result).isPresent();
            Integer id = result.get().getId();

            Optional<LotMaster> found = lotMasterRepository.findById(id);
            assertThat(found).isPresent();
            assertThat(found.get().getLotNumber()).isEqualTo("LOT-001");
        }

        @Test
        @DisplayName("ロット番号で検索できる")
        void canFindByLotNumber() {
            Optional<LotMaster> found = lotMasterRepository.findByLotNumber("LOT-002");
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("品目コードで検索できる")
        void canFindByItemCode() {
            List<LotMaster> found = lotMasterRepository.findByItemCode("ITEM001");
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("存在しないロット番号で検索すると空を返す")
        void returnsEmptyForNonExistent() {
            Optional<LotMaster> found = lotMasterRepository.findByLotNumber("NOTEXIST");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            List<LotMaster> found = lotMasterRepository.findAll();
            assertThat(found).hasSize(3);
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {
        @Test
        @DisplayName("ロットを更新できる")
        void canUpdateLotMaster() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));

            Optional<LotMaster> saved = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(saved).isPresent();
            LotMaster toUpdate = saved.get();
            toUpdate.setRemarks("更新後の備考");
            toUpdate.setQuantity(new BigDecimal("150.00"));
            lotMasterRepository.update(toUpdate);

            Optional<LotMaster> updated = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getRemarks()).isEqualTo("更新後の備考");
            assertThat(updated.get().getQuantity()).isEqualByComparingTo(new BigDecimal("150.00"));
        }
    }

    @Nested
    @DisplayName("削除")
    class Delete {
        @Test
        @DisplayName("ロットを削除できる")
        void canDeleteLotMaster() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));
            lotMasterRepository.save(createLotMaster("LOT-002", "ITEM001", LotType.PURCHASED));

            lotMasterRepository.deleteByLotNumber("LOT-001");

            assertThat(lotMasterRepository.findByLotNumber("LOT-001")).isEmpty();
            assertThat(lotMasterRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("有効期限")
    class Expiration {
        @Test
        @DisplayName("有効期限切れを判定できる")
        void canCheckExpiration() {
            LotMaster expiredLot = LotMaster.builder()
                    .lotNumber("LOT-EXPIRED")
                    .itemCode("ITEM001")
                    .lotType(LotType.PURCHASED)
                    .manufactureDate(LocalDate.of(2023, 1, 1))
                    .expirationDate(LocalDate.of(2023, 12, 31))
                    .quantity(new BigDecimal("50.00"))
                    .warehouseCode("WH001")
                    .build();
            lotMasterRepository.save(expiredLot);

            Optional<LotMaster> found = lotMasterRepository.findByLotNumber("LOT-EXPIRED");
            assertThat(found).isPresent();
            assertThat(found.get().isExpired()).isTrue();
        }

        @Test
        @DisplayName("有効期限内を判定できる")
        void canCheckNotExpired() {
            LotMaster validLot = LotMaster.builder()
                    .lotNumber("LOT-VALID")
                    .itemCode("ITEM001")
                    .lotType(LotType.PURCHASED)
                    .manufactureDate(LocalDate.now())
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantity(new BigDecimal("50.00"))
                    .warehouseCode("WH001")
                    .build();
            lotMasterRepository.save(validLot);

            Optional<LotMaster> found = lotMasterRepository.findByLotNumber("LOT-VALID");
            assertThat(found).isPresent();
            assertThat(found.get().isExpired()).isFalse();
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLock {
        @Test
        @DisplayName("更新時にバージョンがインクリメントされる")
        void versionIncrementedOnUpdate() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));

            Optional<LotMaster> saved = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(saved).isPresent();
            assertThat(saved.get().getVersion()).isEqualTo(1);

            LotMaster toUpdate = saved.get();
            toUpdate.setRemarks("更新後の備考");
            lotMasterRepository.update(toUpdate);

            Optional<LotMaster> updated = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(updated).isPresent();
            assertThat(updated.get().getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("古いバージョンで更新すると失敗する")
        void updateFailsWithOldVersion() {
            lotMasterRepository.save(createLotMaster("LOT-001", "ITEM001", LotType.PURCHASED));

            // 2人のユーザーが同時にロットデータを取得
            Optional<LotMaster> userA = lotMasterRepository.findByLotNumber("LOT-001");
            Optional<LotMaster> userB = lotMasterRepository.findByLotNumber("LOT-001");

            assertThat(userA).isPresent();
            assertThat(userB).isPresent();

            // ユーザーAが先に更新（成功）
            LotMaster updateA = userA.get();
            updateA.setRemarks("ユーザーAの更新");
            int resultA = lotMasterRepository.update(updateA);
            assertThat(resultA).isEqualTo(1);

            // ユーザーBが古いバージョンで更新（失敗）
            LotMaster updateB = userB.get();
            updateB.setRemarks("ユーザーBの更新");
            int resultB = lotMasterRepository.update(updateB);
            assertThat(resultB).isEqualTo(0);

            // データはユーザーAの更新内容
            Optional<LotMaster> finalResult = lotMasterRepository.findByLotNumber("LOT-001");
            assertThat(finalResult).isPresent();
            assertThat(finalResult.get().getRemarks()).isEqualTo("ユーザーAの更新");
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {
        @Autowired
        private LotCompositionRepository lotCompositionRepository;

        @BeforeEach
        void setUpRelationData() {
            // 子ロット（材料）を作成
            lotMasterRepository.save(createLotMaster("LOT-CHILD1", "ITEM001", LotType.PURCHASED));
            lotMasterRepository.save(createLotMaster("LOT-CHILD2", "ITEM001", LotType.PURCHASED));

            // 親ロット（製品）を作成
            LotMaster parentLot = LotMaster.builder()
                    .lotNumber("LOT-PARENT-REL")
                    .itemCode("ITEM002")
                    .lotType(LotType.MANUFACTURED)
                    .manufactureDate(LocalDate.of(2024, 1, 20))
                    .quantity(new BigDecimal("100.00"))
                    .warehouseCode("WH001")
                    .build();
            lotMasterRepository.save(parentLot);
        }

        @Test
        @DisplayName("構成を含めて取得できる")
        void canFindWithCompositions() {
            // ロット構成を追加
            lotCompositionRepository.save(
                    LotComposition.builder()
                            .parentLotNumber("LOT-PARENT-REL")
                            .childLotNumber("LOT-CHILD1")
                            .usedQuantity(new BigDecimal("30.00"))
                            .build());
            lotCompositionRepository.save(
                    LotComposition.builder()
                            .parentLotNumber("LOT-PARENT-REL")
                            .childLotNumber("LOT-CHILD2")
                            .usedQuantity(new BigDecimal("20.00"))
                            .build());

            // リレーション付きで取得
            Optional<LotMaster> found = lotMasterRepository
                    .findByLotNumberWithCompositions("LOT-PARENT-REL");

            assertThat(found).isPresent();
            assertThat(found.get().getChildLotRelations()).hasSize(2);
            assertThat(found.get().getChildLotRelations())
                    .extracting("childLotNumber")
                    .containsExactlyInAnyOrder("LOT-CHILD1", "LOT-CHILD2");
        }

        @Test
        @DisplayName("構成が空の場合も取得できる")
        void canFindWithEmptyCompositions() {
            Optional<LotMaster> found = lotMasterRepository
                    .findByLotNumberWithCompositions("LOT-PARENT-REL");

            assertThat(found).isPresent();
            assertThat(found.get().getChildLotRelations()).isEmpty();
        }
    }
}
