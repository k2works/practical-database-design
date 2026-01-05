package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ChangeHistoryRepository;
import com.example.sms.domain.model.common.ChangeHistory;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 変更履歴リポジトリテスト.
 */
@DisplayName("変更履歴リポジトリ")
class ChangeHistoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ChangeHistoryRepository changeHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"変更履歴データ\"");
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("変更履歴を登録できる")
        void canRegisterChangeHistory() {
            var history = ChangeHistory.builder()
                    .tableName("商品マスタ")
                    .recordId("PRD001")
                    .operationType("UPDATE")
                    .changedAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                    .changedBy("test-user")
                    .beforeData("{\"商品名\":\"旧商品名\"}")
                    .afterData("{\"商品名\":\"新商品名\"}")
                    .changeReason("商品名変更")
                    .build();

            changeHistoryRepository.save(history);

            var result = changeHistoryRepository.findById(history.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getTableName()).isEqualTo("商品マスタ");
            assertThat(result.get().getRecordId()).isEqualTo("PRD001");
        }

        @Test
        @DisplayName("INSERT履歴を登録できる（変更前データなし）")
        void canRegisterInsertHistory() {
            var history = ChangeHistory.builder()
                    .tableName("商品マスタ")
                    .recordId("PRD002")
                    .operationType("INSERT")
                    .changedAt(LocalDateTime.of(2025, 1, 1, 11, 0))
                    .changedBy("test-user")
                    .beforeData(null)
                    .afterData("{\"商品コード\":\"PRD002\",\"商品名\":\"新商品\"}")
                    .changeReason(null)
                    .build();

            changeHistoryRepository.save(history);

            var result = changeHistoryRepository.findById(history.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getOperationType()).isEqualTo("INSERT");
            assertThat(result.get().getBeforeData()).isNull();
        }

        @Test
        @DisplayName("DELETE履歴を登録できる（変更後データなし）")
        void canRegisterDeleteHistory() {
            var history = ChangeHistory.builder()
                    .tableName("商品マスタ")
                    .recordId("PRD003")
                    .operationType("DELETE")
                    .changedAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                    .changedBy("test-user")
                    .beforeData("{\"商品コード\":\"PRD003\",\"商品名\":\"削除商品\"}")
                    .afterData(null)
                    .changeReason("廃番")
                    .build();

            changeHistoryRepository.save(history);

            var result = changeHistoryRepository.findById(history.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getOperationType()).isEqualTo("DELETE");
            assertThat(result.get().getAfterData()).isNull();
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("テーブル名で検索できる")
        void canFindByTableName() {
            var hist1 = createHistory("商品マスタ", "PRD001");
            var hist2 = createHistory("商品マスタ", "PRD002");
            var hist3 = createHistory("顧客マスタ", "CUS001");
            changeHistoryRepository.save(hist1);
            changeHistoryRepository.save(hist2);
            changeHistoryRepository.save(hist3);

            var result = changeHistoryRepository.findByTableName("商品マスタ");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("テーブル名とレコードIDで検索できる")
        void canFindByTableAndRecordId() {
            var hist1 = createHistory("商品マスタ", "PRD001");
            var hist2 = createHistory("商品マスタ", "PRD001");
            var hist3 = createHistory("商品マスタ", "PRD002");
            changeHistoryRepository.save(hist1);
            changeHistoryRepository.save(hist2);
            changeHistoryRepository.save(hist3);

            var result = changeHistoryRepository.findByTableAndRecordId("商品マスタ", "PRD001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("日時範囲で検索できる")
        void canFindByDateRange() {
            var hist1 = createHistory("商品マスタ", "PRD001");
            hist1.setChangedAt(LocalDateTime.of(2025, 1, 5, 10, 0));
            var hist2 = createHistory("商品マスタ", "PRD002");
            hist2.setChangedAt(LocalDateTime.of(2025, 1, 15, 10, 0));
            var hist3 = createHistory("商品マスタ", "PRD003");
            hist3.setChangedAt(LocalDateTime.of(2025, 1, 25, 10, 0));
            changeHistoryRepository.save(hist1);
            changeHistoryRepository.save(hist2);
            changeHistoryRepository.save(hist3);

            var result = changeHistoryRepository.findByDateRange(
                    LocalDateTime.of(2025, 1, 1, 0, 0),
                    LocalDateTime.of(2025, 1, 20, 0, 0));
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("全件取得")
    class FindAll {

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            var hist1 = createHistory("商品マスタ", "PRD001");
            var hist2 = createHistory("顧客マスタ", "CUS001");
            changeHistoryRepository.save(hist1);
            changeHistoryRepository.save(hist2);

            var result = changeHistoryRepository.findAll();
            assertThat(result).hasSize(2);
        }
    }

    private ChangeHistory createHistory(String tableName, String recordId) {
        return ChangeHistory.builder()
                .tableName(tableName)
                .recordId(recordId)
                .operationType("UPDATE")
                .changedAt(LocalDateTime.now())
                .changedBy("test-user")
                .beforeData("{\"key\":\"before\"}")
                .afterData("{\"key\":\"after\"}")
                .changeReason("テスト")
                .build();
    }
}
