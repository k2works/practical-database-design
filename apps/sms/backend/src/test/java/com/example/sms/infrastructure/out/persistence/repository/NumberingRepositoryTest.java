package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.NumberingRepository;
import com.example.sms.domain.model.common.NumberingHistory;
import com.example.sms.domain.model.common.NumberingMaster;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 採番リポジトリテスト.
 */
@DisplayName("採番リポジトリ")
class NumberingRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private NumberingRepository numberingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"採番履歴データ\"");
        jdbcTemplate.execute("DELETE FROM \"採番マスタ\"");
    }

    @Nested
    @DisplayName("採番マスタ")
    class MasterOperations {

        @Test
        @DisplayName("採番マスタを登録できる")
        void canRegisterNumberingMaster() {
            var master = NumberingMaster.builder()
                    .numberingCode("TEST")
                    .numberingName("テスト番号")
                    .prefix("TST")
                    .format("MONTHLY")
                    .digits(4)
                    .currentValue(0L)
                    .resetTarget(true)
                    .build();

            numberingRepository.saveMaster(master);

            var result = numberingRepository.findMasterByNumberingCode("TEST");
            assertThat(result).isPresent();
            assertThat(result.get().getNumberingName()).isEqualTo("テスト番号");
            assertThat(result.get().getPrefix()).isEqualTo("TST");
        }

        @Test
        @DisplayName("採番マスタを更新できる")
        void canUpdateNumberingMaster() {
            var master = createMaster("TEST", "テスト番号");
            numberingRepository.saveMaster(master);

            var fetched = numberingRepository.findMasterByNumberingCode("TEST").get();
            fetched.setCurrentValue(100L);
            fetched.setLastNumberingDate(LocalDate.of(2025, 1, 15));
            numberingRepository.updateMaster(fetched);

            var updated = numberingRepository.findMasterByNumberingCode("TEST").get();
            assertThat(updated.getCurrentValue()).isEqualTo(100L);
            assertThat(updated.getLastNumberingDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        }

        @Test
        @DisplayName("全件取得できる")
        void canFindAllMasters() {
            var master1 = createMaster("TEST1", "テスト1");
            var master2 = createMaster("TEST2", "テスト2");
            numberingRepository.saveMaster(master1);
            numberingRepository.saveMaster(master2);

            var result = numberingRepository.findAllMasters();
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("採番履歴")
    class HistoryOperations {

        @Test
        @DisplayName("採番履歴を登録できる")
        void canRegisterNumberingHistory() {
            var master = createMaster("TEST", "テスト番号");
            numberingRepository.saveMaster(master);

            var history = NumberingHistory.builder()
                    .numberingCode("TEST")
                    .yearMonth("202501")
                    .lastNumber(1L)
                    .build();

            numberingRepository.saveHistory(history);

            var result = numberingRepository.findHistoryByCodeAndYearMonth("TEST", "202501");
            assertThat(result).isPresent();
            assertThat(result.get().getLastNumber()).isEqualTo(1L);
        }

        @Test
        @DisplayName("最終番号をインクリメントできる")
        void canIncrementLastNumber() {
            var master = createMaster("TEST", "テスト番号");
            numberingRepository.saveMaster(master);

            var history = NumberingHistory.builder()
                    .numberingCode("TEST")
                    .yearMonth("202501")
                    .lastNumber(1L)
                    .build();
            numberingRepository.saveHistory(history);

            var fetched = numberingRepository.findHistoryByCodeAndYearMonth("TEST", "202501").get();
            numberingRepository.incrementLastNumber(fetched.getId());

            var updated = numberingRepository.findHistoryByCodeAndYearMonth("TEST", "202501").get();
            assertThat(updated.getLastNumber()).isEqualTo(2L);
        }

        @Test
        @DisplayName("採番コードで履歴一覧を取得できる")
        void canFindHistoriesByNumberingCode() {
            var master = createMaster("TEST", "テスト番号");
            numberingRepository.saveMaster(master);

            var hist1 = NumberingHistory.builder()
                    .numberingCode("TEST")
                    .yearMonth("202501")
                    .lastNumber(10L)
                    .build();
            var hist2 = NumberingHistory.builder()
                    .numberingCode("TEST")
                    .yearMonth("202502")
                    .lastNumber(5L)
                    .build();
            numberingRepository.saveHistory(hist1);
            numberingRepository.saveHistory(hist2);

            var result = numberingRepository.findHistoriesByNumberingCode("TEST");
            assertThat(result).hasSize(2);
        }
    }

    private NumberingMaster createMaster(String code, String name) {
        return NumberingMaster.builder()
                .numberingCode(code)
                .numberingName(name)
                .prefix("TST")
                .format("MONTHLY")
                .digits(4)
                .currentValue(0L)
                .resetTarget(false)
                .build();
    }
}
