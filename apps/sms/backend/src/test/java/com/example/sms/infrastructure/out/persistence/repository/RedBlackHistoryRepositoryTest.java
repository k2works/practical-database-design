package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.RedBlackHistoryRepository;
import com.example.sms.domain.model.common.RedBlackHistory;
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
 * 赤黒処理履歴リポジトリテスト.
 */
@DisplayName("赤黒処理履歴リポジトリ")
class RedBlackHistoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private RedBlackHistoryRepository redBlackHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM \"赤黒処理履歴データ\"");
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("赤黒処理履歴を登録できる")
        void canRegisterRedBlackHistory() {
            var history = RedBlackHistory.builder()
                    .processNumber("RB-20250101-0001")
                    .processDateTime(LocalDateTime.of(2025, 1, 1, 10, 0))
                    .slipCategory("売上")
                    .originalSlipNumber("SL-202501-0001")
                    .redSlipNumber("SL-202501-0002")
                    .blackSlipNumber("SL-202501-0003")
                    .processReason("金額訂正")
                    .processedBy("test-user")
                    .build();

            redBlackHistoryRepository.save(history);

            var result = redBlackHistoryRepository.findByProcessNumber("RB-20250101-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getProcessNumber()).isEqualTo("RB-20250101-0001");
            assertThat(result.get().getSlipCategory()).isEqualTo("売上");
        }

        @Test
        @DisplayName("黒伝票なしで登録できる（取消の場合）")
        void canRegisterWithoutBlackSlip() {
            var history = RedBlackHistory.builder()
                    .processNumber("RB-20250101-0002")
                    .processDateTime(LocalDateTime.of(2025, 1, 1, 11, 0))
                    .slipCategory("売上")
                    .originalSlipNumber("SL-202501-0004")
                    .redSlipNumber("SL-202501-0005")
                    .blackSlipNumber(null)
                    .processReason("取消")
                    .processedBy("test-user")
                    .build();

            redBlackHistoryRepository.save(history);

            var result = redBlackHistoryRepository.findByProcessNumber("RB-20250101-0002");
            assertThat(result).isPresent();
            assertThat(result.get().getBlackSlipNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("元伝票番号で検索できる")
        void canFindByOriginalSlipNumber() {
            var hist1 = createHistory("RB-20250101-0001", "SL-202501-0001");
            var hist2 = createHistory("RB-20250101-0002", "SL-202501-0001");
            var hist3 = createHistory("RB-20250101-0003", "SL-202501-0099");
            redBlackHistoryRepository.save(hist1);
            redBlackHistoryRepository.save(hist2);
            redBlackHistoryRepository.save(hist3);

            var result = redBlackHistoryRepository.findByOriginalSlipNumber("SL-202501-0001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("伝票種別で検索できる")
        void canFindBySlipCategory() {
            var hist1 = createHistory("RB-20250101-0001", "SL-202501-0001");
            hist1.setSlipCategory("売上");
            var hist2 = createHistory("RB-20250101-0002", "PU-202501-0001");
            hist2.setSlipCategory("仕入");
            redBlackHistoryRepository.save(hist1);
            redBlackHistoryRepository.save(hist2);

            var result = redBlackHistoryRepository.findBySlipCategory("売上");
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProcessNumber()).isEqualTo("RB-20250101-0001");
        }

        @Test
        @DisplayName("処理日時範囲で検索できる")
        void canFindByProcessDateTimeBetween() {
            var hist1 = createHistory("RB-20250101-0001", "SL-202501-0001");
            hist1.setProcessDateTime(LocalDateTime.of(2025, 1, 5, 10, 0));
            var hist2 = createHistory("RB-20250101-0002", "SL-202501-0002");
            hist2.setProcessDateTime(LocalDateTime.of(2025, 1, 15, 10, 0));
            var hist3 = createHistory("RB-20250101-0003", "SL-202501-0003");
            hist3.setProcessDateTime(LocalDateTime.of(2025, 1, 25, 10, 0));
            redBlackHistoryRepository.save(hist1);
            redBlackHistoryRepository.save(hist2);
            redBlackHistoryRepository.save(hist3);

            var result = redBlackHistoryRepository.findByProcessDateTimeBetween(
                    LocalDateTime.of(2025, 1, 1, 0, 0),
                    LocalDateTime.of(2025, 1, 20, 0, 0));
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("最新の処理番号を取得できる")
        void canFindLatestProcessNumber() {
            var hist1 = createHistory("RB-20250101-0001", "SL-202501-0001");
            var hist2 = createHistory("RB-20250101-0002", "SL-202501-0002");
            var hist3 = createHistory("RB-20250101-0010", "SL-202501-0003");
            redBlackHistoryRepository.save(hist1);
            redBlackHistoryRepository.save(hist2);
            redBlackHistoryRepository.save(hist3);

            var result = redBlackHistoryRepository.findLatestProcessNumber("RB-20250101-%");
            assertThat(result).isEqualTo("RB-20250101-0010");
        }
    }

    @Nested
    @DisplayName("全件取得")
    class FindAll {

        @Test
        @DisplayName("全件取得できる")
        void canFindAll() {
            var hist1 = createHistory("RB-20250101-0001", "SL-202501-0001");
            var hist2 = createHistory("RB-20250101-0002", "SL-202501-0002");
            redBlackHistoryRepository.save(hist1);
            redBlackHistoryRepository.save(hist2);

            var result = redBlackHistoryRepository.findAll();
            assertThat(result).hasSize(2);
        }
    }

    private RedBlackHistory createHistory(String processNumber, String originalSlipNumber) {
        return RedBlackHistory.builder()
                .processNumber(processNumber)
                .processDateTime(LocalDateTime.now())
                .slipCategory("売上")
                .originalSlipNumber(originalSlipNumber)
                .redSlipNumber("RED-" + originalSlipNumber)
                .blackSlipNumber("BLACK-" + originalSlipNumber)
                .processReason("テスト")
                .processedBy("test-user")
                .build();
    }
}
