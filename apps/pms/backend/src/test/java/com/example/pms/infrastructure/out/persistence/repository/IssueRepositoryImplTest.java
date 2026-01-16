package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueDetailRepository;
import com.example.pms.application.port.out.IssueRepository;
import com.example.pms.domain.model.inventory.Issue;
import com.example.pms.domain.model.inventory.IssueDetail;
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
 * 払出リポジトリテスト.
 */
@DisplayName("払出リポジトリ")
class IssueRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueDetailRepository issueDetailRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        issueDetailRepository.deleteAll();
        issueRepository.deleteAll();

        // Create required master data
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC001', 'テスト場所1', '倉庫')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "場所マスタ" ("場所コード", "場所名", "場所区分")
            VALUES ('LOC002', 'テスト場所2', '製造')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目1', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード")
            VALUES ('ORD001', '製造', 'ITEM001', '2024-01-15', '2024-02-01', 100, 'LOC001')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "作業指示データ" ("作業指示番号", "オーダ番号", "作業指示日", "品目コード",
                "作業指示数", "場所コード", "開始予定日", "完成予定日")
            VALUES ('WO-001', 'ORD001', '2024-01-15', 'ITEM001', 100, 'LOC001', '2024-01-15', '2024-01-20')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "作業指示データ" ("作業指示番号", "オーダ番号", "作業指示日", "品目コード",
                "作業指示数", "場所コード", "開始予定日", "完成予定日")
            VALUES ('WO-002', 'ORD001', '2024-01-16', 'ITEM001', 50, 'LOC002', '2024-01-16', '2024-01-21')
            ON CONFLICT DO NOTHING
            """);
    }

    private Issue createIssue(String issueNumber, String workOrderNumber, String locationCode) {
        return Issue.builder()
                .issueNumber(issueNumber)
                .workOrderNumber(workOrderNumber)
                .routingSequence(1)
                .locationCode(locationCode)
                .issueDate(LocalDate.of(2024, 1, 15))
                .issuerCode("USER001")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("払出を登録できる")
        void canRegisterIssue() {
            // Arrange
            Issue issue = createIssue("ISS-001", "WO-001", "LOC001");

            // Act
            issueRepository.save(issue);

            // Assert
            Optional<Issue> found = issueRepository.findByIssueNumber("ISS-001");
            assertThat(found).isPresent();
            assertThat(found.get().getWorkOrderNumber()).isEqualTo("WO-001");
            assertThat(found.get().getLocationCode()).isEqualTo("LOC001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            issueRepository.save(createIssue("ISS-001", "WO-001", "LOC001"));
            issueRepository.save(createIssue("ISS-002", "WO-001", "LOC002"));
            issueRepository.save(createIssue("ISS-003", "WO-002", "LOC001"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<Issue> issue = issueRepository.findByIssueNumber("ISS-001");
            assertThat(issue).isPresent();
            Integer id = issue.get().getId();

            // Act
            Optional<Issue> found = issueRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getIssueNumber()).isEqualTo("ISS-001");
        }

        @Test
        @DisplayName("払出番号で検索できる")
        void canFindByIssueNumber() {
            // Act
            Optional<Issue> found = issueRepository.findByIssueNumber("ISS-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getLocationCode()).isEqualTo("LOC002");
        }

        @Test
        @DisplayName("作業指示番号で検索できる")
        void canFindByWorkOrderNumber() {
            // Act
            List<Issue> found = issueRepository.findByWorkOrderNumber("WO-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(i -> "WO-001".equals(i.getWorkOrderNumber()));
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocationCode() {
            // Act
            List<Issue> found = issueRepository.findByLocationCode("LOC001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(i -> "LOC001".equals(i.getLocationCode()));
        }

        @Test
        @DisplayName("存在しない払出番号で検索すると空を返す")
        void returnsEmptyForNonExistentIssueNumber() {
            // Act
            Optional<Issue> found = issueRepository.findByIssueNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<Issue> all = issueRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }

    @Nested
    @DisplayName("リレーション")
    class Relation {

        private IssueDetail createDetail(String issueNumber, int lineNumber, String itemCode) {
            return IssueDetail.builder()
                    .issueNumber(issueNumber)
                    .lineNumber(lineNumber)
                    .itemCode(itemCode)
                    .issueQuantity(new BigDecimal("10.00"))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();
        }

        @Test
        @DisplayName("明細を含めて取得できる")
        void canFindWithDetails() {
            // Arrange
            Issue issue = createIssue("ISS-REL-001", "WO-001", "LOC001");
            issueRepository.save(issue);

            IssueDetail detail1 = createDetail("ISS-REL-001", 1, "ITEM001");
            IssueDetail detail2 = createDetail("ISS-REL-001", 2, "ITEM001");
            issueDetailRepository.save(detail1);
            issueDetailRepository.save(detail2);

            // Act
            Optional<Issue> found = issueRepository.findByIssueNumberWithDetails("ISS-REL-001");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDetails()).hasSize(2);
            assertThat(found.get().getDetails().get(0).getLineNumber()).isEqualTo(1);
            assertThat(found.get().getDetails().get(1).getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない場合は空のリストを返す")
        void returnsEmptyListWhenNoDetails() {
            // Arrange
            Issue issue = createIssue("ISS-REL-002", "WO-001", "LOC001");
            issueRepository.save(issue);

            // Act
            Optional<Issue> found = issueRepository.findByIssueNumberWithDetails("ISS-REL-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDetails()).isEmpty();
        }
    }
}
