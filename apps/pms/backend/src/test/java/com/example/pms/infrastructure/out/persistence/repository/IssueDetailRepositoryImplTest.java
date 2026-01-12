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
 * 払出明細リポジトリテスト.
 */
@DisplayName("払出明細リポジトリ")
class IssueDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private IssueDetailRepository issueDetailRepository;

    @Autowired
    private IssueRepository issueRepository;

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
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM001', '2024-01-01', 'テスト品目1', '製品')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "品目マスタ" ("品目コード", "適用開始日", "品名", "品目区分")
            VALUES ('ITEM002', '2024-01-01', 'テスト品目2', '部品')
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

        // Create parent issue
        issueRepository.save(Issue.builder()
                .issueNumber("ISS-001")
                .workOrderNumber("WO-001")
                .routingSequence(1)
                .locationCode("LOC001")
                .issueDate(LocalDate.of(2024, 1, 15))
                .issuerCode("USER001")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build());
    }

    private IssueDetail createIssueDetail(String issueNumber, Integer lineNumber,
                                           String itemCode, BigDecimal quantity) {
        return IssueDetail.builder()
                .issueNumber(issueNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .issueQuantity(quantity)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("払出明細を登録できる")
        void canRegisterIssueDetail() {
            // Arrange
            IssueDetail detail = createIssueDetail("ISS-001", 1, "ITEM001", new BigDecimal("50.00"));

            // Act
            issueDetailRepository.save(detail);

            // Assert
            Optional<IssueDetail> found =
                    issueDetailRepository.findByIssueNumberAndLineNumber("ISS-001", 1);
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
            assertThat(found.get().getIssueQuantity()).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            issueDetailRepository.save(createIssueDetail("ISS-001", 1, "ITEM001", new BigDecimal("50.00")));
            issueDetailRepository.save(createIssueDetail("ISS-001", 2, "ITEM002", new BigDecimal("30.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<IssueDetail> detail =
                    issueDetailRepository.findByIssueNumberAndLineNumber("ISS-001", 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<IssueDetail> found = issueDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("払出番号と行番号で検索できる")
        void canFindByIssueNumberAndLineNumber() {
            // Act
            Optional<IssueDetail> found =
                    issueDetailRepository.findByIssueNumberAndLineNumber("ISS-001", 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("払出番号で検索できる")
        void canFindByIssueNumber() {
            // Act
            List<IssueDetail> found = issueDetailRepository.findByIssueNumber("ISS-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(d -> "ISS-001".equals(d.getIssueNumber()));
        }

        @Test
        @DisplayName("存在しない払出番号で検索すると空を返す")
        void returnsEmptyForNonExistentIssueNumber() {
            // Act
            Optional<IssueDetail> found =
                    issueDetailRepository.findByIssueNumberAndLineNumber("NOTEXIST", 1);

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<IssueDetail> all = issueDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
