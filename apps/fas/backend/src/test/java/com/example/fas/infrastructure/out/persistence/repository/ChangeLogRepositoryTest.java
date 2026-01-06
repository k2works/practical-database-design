package com.example.fas.infrastructure.out.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fas.application.port.out.ChangeLogRepository;
import com.example.fas.domain.model.audit.ChangeLog;
import com.example.fas.domain.model.audit.ChangeLog.OperationType;
import com.example.fas.testsetup.BaseIntegrationTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 変更ログリポジトリテスト.
 */
@DisplayName("変更ログリポジトリ")
class ChangeLogRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @BeforeEach
    void setUp() {
        changeLogRepository.deleteAll();
    }

    @Nested
    @DisplayName("登録")
    class InsertTests {

        @Test
        @DisplayName("変更ログを登録できる")
        void canInsertChangeLog() {
            // Arrange
            var changeLog = ChangeLog.builder()
                    .tableName("勘定科目マスタ")
                    .recordKey("11100")
                    .operationType(OperationType.INSERT)
                    .afterData("{\"勘定科目コード\":\"11100\",\"勘定科目名\":\"現金\"}")
                    .operatedBy("admin")
                    .build();

            // Act
            changeLogRepository.save(changeLog);

            // Assert
            assertThat(changeLog.getLogId()).isNotNull();
            var result = changeLogRepository.findById(changeLog.getLogId());
            assertThat(result).isPresent();
            assertThat(result.get().getTableName()).isEqualTo("勘定科目マスタ");
            assertThat(result.get().getOperationType()).isEqualTo(OperationType.INSERT);
        }

        @Test
        @DisplayName("UPDATE操作のログを登録できる")
        void canInsertUpdateLog() {
            // Arrange
            var changeLog = ChangeLog.builder()
                    .tableName("勘定科目マスタ")
                    .recordKey("11100")
                    .operationType(OperationType.UPDATE)
                    .beforeData("{\"勘定科目名\":\"現金\"}")
                    .afterData("{\"勘定科目名\":\"現金及び預金\"}")
                    .operatedBy("admin")
                    .remarks("科目名変更")
                    .build();

            // Act
            changeLogRepository.save(changeLog);

            // Assert
            var result = changeLogRepository.findById(changeLog.getLogId());
            assertThat(result).isPresent();
            assertThat(result.get().getOperationType()).isEqualTo(OperationType.UPDATE);
            assertThat(result.get().getBeforeData()).contains("現金");
            assertThat(result.get().getAfterData()).contains("現金及び預金");
        }

        @Test
        @DisplayName("DELETE操作のログを登録できる")
        void canInsertDeleteLog() {
            // Arrange
            var changeLog = ChangeLog.builder()
                    .tableName("勘定科目マスタ")
                    .recordKey("99999")
                    .operationType(OperationType.DELETE)
                    .beforeData("{\"勘定科目コード\":\"99999\"}")
                    .operatedBy("admin")
                    .build();

            // Act
            changeLogRepository.save(changeLog);

            // Assert
            var result = changeLogRepository.findById(changeLog.getLogId());
            assertThat(result).isPresent();
            assertThat(result.get().getOperationType()).isEqualTo(OperationType.DELETE);
            assertThat(result.get().getAfterData()).isNull();
        }
    }

    @Nested
    @DisplayName("検索")
    class SearchTests {

        @BeforeEach
        void setUpTestData() {
            // テストデータを登録
            changeLogRepository.save(ChangeLog.builder()
                    .tableName("勘定科目マスタ")
                    .recordKey("11100")
                    .operationType(OperationType.INSERT)
                    .afterData("{\"勘定科目コード\":\"11100\"}")
                    .operatedAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                    .operatedBy("user1")
                    .build());

            changeLogRepository.save(ChangeLog.builder()
                    .tableName("勘定科目マスタ")
                    .recordKey("11100")
                    .operationType(OperationType.UPDATE)
                    .beforeData("{\"勘定科目名\":\"現金\"}")
                    .afterData("{\"勘定科目名\":\"現金及び預金\"}")
                    .operatedAt(LocalDateTime.of(2025, 1, 15, 14, 30))
                    .operatedBy("user2")
                    .build());

            changeLogRepository.save(ChangeLog.builder()
                    .tableName("部門マスタ")
                    .recordKey("00001")
                    .operationType(OperationType.INSERT)
                    .afterData("{\"部門コード\":\"00001\"}")
                    .operatedAt(LocalDateTime.of(2025, 1, 10, 9, 0))
                    .operatedBy("user1")
                    .build());
        }

        @Test
        @DisplayName("テーブル名とレコードキーで検索できる")
        void canFindByTableNameAndRecordKey() {
            // Act
            var result = changeLogRepository.findByTableNameAndRecordKey(
                    "勘定科目マスタ", "11100");

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(log ->
                    "勘定科目マスタ".equals(log.getTableName())
                            && "11100".equals(log.getRecordKey()));
        }

        @Test
        @DisplayName("テーブル名で検索できる")
        void canFindByTableName() {
            // Act
            var result = changeLogRepository.findByTableName("勘定科目マスタ");

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(log -> "勘定科目マスタ".equals(log.getTableName()));
        }

        @Test
        @DisplayName("操作種別で検索できる")
        void canFindByOperationType() {
            // Act
            var result = changeLogRepository.findByOperationType(OperationType.INSERT);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(log -> log.getOperationType() == OperationType.INSERT);
        }

        @Test
        @DisplayName("期間指定で検索できる")
        void canFindByDateRange() {
            // Act
            var result = changeLogRepository.findByDateRange(
                    LocalDateTime.of(2025, 1, 5, 0, 0),
                    LocalDateTime.of(2025, 1, 20, 0, 0));

            // Assert
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("操作者で検索できる")
        void canFindByOperatedBy() {
            // Act
            var result = changeLogRepository.findByOperatedBy("user1");

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(log -> "user1".equals(log.getOperatedBy()));
        }

        @Test
        @DisplayName("複合条件で検索できる")
        void canFindByConditions() {
            // Act
            var result = changeLogRepository.findByConditions(
                    "勘定科目マスタ",
                    null,
                    OperationType.UPDATE,
                    null,
                    null,
                    null);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOperationType()).isEqualTo(OperationType.UPDATE);
        }
    }
}
