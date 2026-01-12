package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.IssueInstructionRepository;
import com.example.pms.domain.model.inventory.IssueInstruction;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 払出指示リポジトリテスト.
 */
@DisplayName("払出指示リポジトリ")
class IssueInstructionRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private IssueInstructionRepository issueInstructionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        issueInstructionRepository.deleteAll();

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
            INSERT INTO "基準生産計画" ("MPS番号", "計画日", "品目コード", "計画数量", "納期")
            VALUES ('MPS001', '2024-01-01', 'ITEM001', 100, '2024-02-01')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード")
            VALUES ('ORD001', '製造', 'ITEM001', '2024-01-15', '2024-02-01', 100, 'LOC001')
            ON CONFLICT DO NOTHING
            """);
        jdbcTemplate.execute("""
            INSERT INTO "オーダ情報" ("オーダNO", "オーダ種別", "品目コード", "着手予定日", "納期", "計画数量", "場所コード")
            VALUES ('ORD002', '製造', 'ITEM002', '2024-01-15', '2024-02-15', 50, 'LOC001')
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
    }

    private IssueInstruction createIssueInstruction(String instructionNumber, String orderNumber,
                                                      String locationCode) {
        return IssueInstruction.builder()
                .instructionNumber(instructionNumber)
                .orderNumber(orderNumber)
                .instructionDate(LocalDate.of(2024, 1, 15))
                .locationCode(locationCode)
                .remarks("テスト備考")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("払出指示を登録できる")
        void canRegisterIssueInstruction() {
            // Arrange
            IssueInstruction instruction = createIssueInstruction("ISS-001", "ORD001", "LOC001");

            // Act
            issueInstructionRepository.save(instruction);

            // Assert
            Optional<IssueInstruction> found = issueInstructionRepository.findByInstructionNumber("ISS-001");
            assertThat(found).isPresent();
            assertThat(found.get().getOrderNumber()).isEqualTo("ORD001");
            assertThat(found.get().getLocationCode()).isEqualTo("LOC001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            issueInstructionRepository.save(createIssueInstruction("ISS-001", "ORD001", "LOC001"));
            issueInstructionRepository.save(createIssueInstruction("ISS-002", "ORD001", "LOC002"));
            issueInstructionRepository.save(createIssueInstruction("ISS-003", "ORD002", "LOC001"));
        }

        @Test
        @DisplayName("IDで検索できる")
        void canFindById() {
            // Arrange
            Optional<IssueInstruction> instruction = issueInstructionRepository.findByInstructionNumber("ISS-001");
            assertThat(instruction).isPresent();
            Integer id = instruction.get().getId();

            // Act
            Optional<IssueInstruction> found = issueInstructionRepository.findById(id);

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getInstructionNumber()).isEqualTo("ISS-001");
        }

        @Test
        @DisplayName("払出指示番号で検索できる")
        void canFindByInstructionNumber() {
            // Act
            Optional<IssueInstruction> found = issueInstructionRepository.findByInstructionNumber("ISS-002");

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getLocationCode()).isEqualTo("LOC002");
        }

        @Test
        @DisplayName("オーダ番号で検索できる")
        void canFindByOrderNumber() {
            // Act
            List<IssueInstruction> found = issueInstructionRepository.findByOrderNumber("ORD001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(i -> "ORD001".equals(i.getOrderNumber()));
        }

        @Test
        @DisplayName("場所コードで検索できる")
        void canFindByLocationCode() {
            // Act
            List<IssueInstruction> found = issueInstructionRepository.findByLocationCode("LOC001");

            // Assert
            assertThat(found).hasSize(2);
            assertThat(found).allMatch(i -> "LOC001".equals(i.getLocationCode()));
        }

        @Test
        @DisplayName("存在しない払出指示番号で検索すると空を返す")
        void returnsEmptyForNonExistentInstructionNumber() {
            // Act
            Optional<IssueInstruction> found = issueInstructionRepository.findByInstructionNumber("NOTEXIST");

            // Assert
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            // Act
            List<IssueInstruction> all = issueInstructionRepository.findAll();

            // Assert
            assertThat(all).hasSize(3);
        }
    }
}
