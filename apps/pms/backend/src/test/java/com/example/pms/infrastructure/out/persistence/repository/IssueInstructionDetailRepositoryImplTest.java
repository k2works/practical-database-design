package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueInstructionDetailRepository;
import com.example.pms.application.port.out.IssueInstructionRepository;
import com.example.pms.domain.model.inventory.IssueInstruction;
import com.example.pms.domain.model.inventory.IssueInstructionDetail;
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
 * 払出指示明細リポジトリテスト.
 */
@DisplayName("払出指示明細リポジトリ")
class IssueInstructionDetailRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private IssueInstructionDetailRepository issueInstructionDetailRepository;

    @Autowired
    private IssueInstructionRepository issueInstructionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        issueInstructionDetailRepository.deleteAll();
        issueInstructionRepository.deleteAll();

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

        // Create parent issue instruction
        issueInstructionRepository.save(IssueInstruction.builder()
                .instructionNumber("ISS-001")
                .orderNumber("ORD001")
                .instructionDate(LocalDate.of(2024, 1, 15))
                .locationCode("LOC001")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build());
    }

    private IssueInstructionDetail createIssueInstructionDetail(String instructionNumber, Integer lineNumber,
                                                                  String itemCode, BigDecimal quantity) {
        return IssueInstructionDetail.builder()
                .instructionNumber(instructionNumber)
                .lineNumber(lineNumber)
                .itemCode(itemCode)
                .routingSequence(1)
                .issueQuantity(quantity)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("払出指示明細を登録できる")
        void canRegisterIssueInstructionDetail() {
            // Arrange
            IssueInstructionDetail detail = createIssueInstructionDetail("ISS-001", 1, "ITEM001",
                    new BigDecimal("50.00"));

            // Act
            issueInstructionDetailRepository.save(detail);

            // Assert
            Optional<IssueInstructionDetail> found =
                    issueInstructionDetailRepository.findByInstructionNumberAndLineNumber("ISS-001", 1);
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
            issueInstructionDetailRepository.save(createIssueInstructionDetail("ISS-001", 1, "ITEM001",
                    new BigDecimal("50.00")));
            issueInstructionDetailRepository.save(createIssueInstructionDetail("ISS-001", 2, "ITEM002",
                    new BigDecimal("30.00")));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<IssueInstructionDetail> detail =
                    issueInstructionDetailRepository.findByInstructionNumberAndLineNumber("ISS-001", 1);
            assertThat(detail).isPresent();
            Integer id = detail.get().getId();

            // Act
            Optional<IssueInstructionDetail> found = issueInstructionDetailRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM001");
        }

        @Test
        @DisplayName("払出指示番号と行番号で検索できる")
        void canFindByInstructionNumberAndLineNumber() {
            // Act
            Optional<IssueInstructionDetail> found =
                    issueInstructionDetailRepository.findByInstructionNumberAndLineNumber("ISS-001", 2);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getItemCode()).isEqualTo("ITEM002");
        }

        @Test
        @DisplayName("払出指示番号で検索できる")
        void canFindByInstructionNumber() {
            // Act
            List<IssueInstructionDetail> found =
                    issueInstructionDetailRepository.findByInstructionNumber("ISS-001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(d -> "ISS-001".equals(d.getInstructionNumber()));
        }

        @Test
        @DisplayName("存在しない払出指示番号で検索すると空を返す")
        void returnsEmptyForNonExistentInstructionNumber() {
            // Act
            Optional<IssueInstructionDetail> found =
                    issueInstructionDetailRepository.findByInstructionNumberAndLineNumber("NOTEXIST", 1);

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<IssueInstructionDetail> all = issueInstructionDetailRepository.findAll();

            // Assert
            assertThat(all).hasSize(2);
        }
    }
}
