package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PaymentRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentDetail;
import com.example.sms.domain.model.payment.PaymentMethod;
import com.example.sms.domain.model.payment.PaymentStatus;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 支払リポジトリテスト.
 */
@DisplayName("支払リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class PaymentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 支払関連データを削除
        jdbcTemplate.execute("DELETE FROM \"支払明細データ\"");
        jdbcTemplate.execute("DELETE FROM \"支払データ\"");

        // 仕入関連データを削除（FK制約があるため順番に削除）
        jdbcTemplate.execute("DELETE FROM \"仕入明細データ\"");
        jdbcTemplate.execute("DELETE FROM \"仕入データ\"");
        jdbcTemplate.execute("DELETE FROM \"入荷明細データ\"");
        jdbcTemplate.execute("DELETE FROM \"入荷データ\"");
        jdbcTemplate.execute("DELETE FROM \"発注明細データ\"");
        jdbcTemplate.execute("DELETE FROM \"発注データ\"");

        // 取引先マスタに仕入先を登録
        jdbcTemplate.update(
                "INSERT INTO \"取引先マスタ\" (\"取引先コード\", \"取引先名\", \"仕入先区分\") "
                        + "VALUES (?, ?, ?) "
                        + "ON CONFLICT (\"取引先コード\") DO NOTHING",
                "SUP001", "テスト仕入先", true);

        // 仕入先マスタを登録
        jdbcTemplate.update(
                "INSERT INTO \"仕入先マスタ\" (\"仕入先コード\", \"仕入先枝番\") "
                        + "VALUES (?, ?) "
                        + "ON CONFLICT (\"仕入先コード\", \"仕入先枝番\") DO NOTHING",
                "SUP001", "00");

        // 倉庫マスタを登録
        jdbcTemplate.update(
                "INSERT INTO \"倉庫マスタ\" (\"倉庫コード\", \"倉庫名\") "
                        + "VALUES (?, ?) "
                        + "ON CONFLICT (\"倉庫コード\") DO NOTHING",
                "WH001", "テスト倉庫");

        // 商品マスタを登録（発注明細で必要）
        jdbcTemplate.update(
                "INSERT INTO \"商品マスタ\" (\"商品コード\", \"商品正式名\", \"商品名\", \"販売単価\", \"仕入単価\") "
                        + "VALUES (?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"商品コード\") DO NOTHING",
                "PRD001", "テスト商品", "テスト", new BigDecimal("1000"), new BigDecimal("800"));

        // 発注データを登録
        jdbcTemplate.update(
                "INSERT INTO \"発注データ\" (\"ID\", \"発注番号\", \"発注日\", \"仕入先コード\", \"仕入先枝番\", "
                        + "\"発注担当者コード\", \"発注合計金額\", \"税額\", \"希望納期\", \"発注ステータス\", \"作成者\", \"更新者\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::発注ステータス, ?, ?) "
                        + "ON CONFLICT (\"発注番号\") DO NOTHING",
                1, "PO-202501-0001", LocalDate.of(2025, 1, 10), "SUP001", "00",
                "EMP001", new BigDecimal("100000"), new BigDecimal("10000"),
                LocalDate.of(2025, 1, 20), "確定", "test-user", "test-user");

        // 入荷データを登録
        jdbcTemplate.update(
                "INSERT INTO \"入荷データ\" (\"ID\", \"入荷番号\", \"発注ID\", \"仕入先コード\", \"仕入先枝番\", "
                        + "\"入荷日\", \"入荷ステータス\", \"倉庫コード\", \"作成者\", \"更新者\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?::入荷ステータス, ?, ?, ?) "
                        + "ON CONFLICT (\"入荷番号\") DO NOTHING",
                1, "RCV-202501-0001", 1, "SUP001", "00",
                LocalDate.of(2025, 1, 15), "検品完了", "WH001", "test-user", "test-user");

        // 仕入データを登録
        jdbcTemplate.update(
                "INSERT INTO \"仕入データ\" (\"仕入番号\", \"入荷ID\", \"仕入先コード\", \"仕入先枝番\", \"仕入日\", "
                        + "\"仕入合計金額\", \"税額\", \"作成者\", \"更新者\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"仕入番号\") DO NOTHING",
                "PUR-202501-0001", 1, "SUP001", "00", LocalDate.of(2025, 1, 15),
                new BigDecimal("100000"), new BigDecimal("10000"), "test-user", "test-user");

        // 2件目の仕入データを登録（明細テスト用）
        jdbcTemplate.update(
                "INSERT INTO \"入荷データ\" (\"ID\", \"入荷番号\", \"発注ID\", \"仕入先コード\", \"仕入先枝番\", "
                        + "\"入荷日\", \"入荷ステータス\", \"倉庫コード\", \"作成者\", \"更新者\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?::入荷ステータス, ?, ?, ?) "
                        + "ON CONFLICT (\"入荷番号\") DO NOTHING",
                2, "RCV-202501-0002", 1, "SUP001", "00",
                LocalDate.of(2025, 1, 20), "検品完了", "WH001", "test-user", "test-user");

        jdbcTemplate.update(
                "INSERT INTO \"仕入データ\" (\"仕入番号\", \"入荷ID\", \"仕入先コード\", \"仕入先枝番\", \"仕入日\", "
                        + "\"仕入合計金額\", \"税額\", \"作成者\", \"更新者\") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (\"仕入番号\") DO NOTHING",
                "PUR-202501-0002", 2, "SUP001", "00", LocalDate.of(2025, 1, 20),
                new BigDecimal("50000"), new BigDecimal("5000"), "test-user", "test-user");
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("支払を登録できる")
        void canRegisterPayment() {
            var payment = Payment.builder()
                    .paymentNumber("PAY-202501-0001")
                    .supplierCode("SUP001")
                    .paymentClosingDate(LocalDate.of(2025, 1, 31))
                    .paymentDueDate(LocalDate.of(2025, 2, 28))
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .paymentAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .withholdingAmount(BigDecimal.ZERO)
                    .netPaymentAmount(new BigDecimal("110000"))
                    .status(PaymentStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            paymentRepository.save(payment);

            var result = paymentRepository.findByPaymentNumber("PAY-202501-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getPaymentNumber()).isEqualTo("PAY-202501-0001");
            assertThat(result.get().getStatus()).isEqualTo(PaymentStatus.DRAFT);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("支払明細付きで登録できる")
        void canRegisterPaymentWithDetails() {
            var payment = Payment.builder()
                    .paymentNumber("PAY-202501-0002")
                    .supplierCode("SUP001")
                    .paymentClosingDate(LocalDate.of(2025, 1, 31))
                    .paymentDueDate(LocalDate.of(2025, 2, 28))
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .paymentAmount(new BigDecimal("100000"))
                    .taxAmount(new BigDecimal("10000"))
                    .withholdingAmount(BigDecimal.ZERO)
                    .netPaymentAmount(new BigDecimal("110000"))
                    .status(PaymentStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PaymentDetail.builder()
                                    .purchaseNumber("PUR-202501-0001")
                                    .purchaseDate(LocalDate.of(2025, 1, 15))
                                    .purchaseAmount(new BigDecimal("100000"))
                                    .taxAmount(new BigDecimal("10000"))
                                    .paymentTargetAmount(new BigDecimal("110000"))
                                    .build()
                    ))
                    .build();

            paymentRepository.save(payment);

            var result = paymentRepository.findWithDetailsByPaymentNumber("PAY-202501-0002");
            assertThat(result).isPresent();
            assertThat(result.get().getDetails()).hasSize(1);
            assertThat(result.get().getDetails().get(0).getPurchaseNumber()).isEqualTo("PUR-202501-0001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("仕入先コードで検索できる")
        void canFindBySupplierCode() {
            var pay1 = createPayment("PAY-202501-0001", LocalDate.of(2025, 1, 31));
            var pay2 = createPayment("PAY-202501-0002", LocalDate.of(2025, 2, 28));
            paymentRepository.save(pay1);
            paymentRepository.save(pay2);

            var result = paymentRepository.findBySupplierCode("SUP001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var pay1 = createPayment("PAY-202501-0001", LocalDate.of(2025, 1, 31));
            pay1.setStatus(PaymentStatus.APPROVED);
            var pay2 = createPayment("PAY-202501-0002", LocalDate.of(2025, 2, 28));
            pay2.setStatus(PaymentStatus.DRAFT);
            paymentRepository.save(pay1);
            paymentRepository.save(pay2);

            var result = paymentRepository.findByStatus(PaymentStatus.APPROVED);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPaymentNumber()).isEqualTo("PAY-202501-0001");
        }

        @Test
        @DisplayName("支払予定日範囲で検索できる")
        void canFindByPaymentDueDateBetween() {
            var pay1 = createPayment("PAY-202501-0001", LocalDate.of(2025, 1, 31));
            pay1.setPaymentDueDate(LocalDate.of(2025, 2, 10));
            var pay2 = createPayment("PAY-202501-0002", LocalDate.of(2025, 2, 28));
            pay2.setPaymentDueDate(LocalDate.of(2025, 2, 20));
            var pay3 = createPayment("PAY-202502-0001", LocalDate.of(2025, 3, 31));
            pay3.setPaymentDueDate(LocalDate.of(2025, 3, 15));
            paymentRepository.save(pay1);
            paymentRepository.save(pay2);
            paymentRepository.save(pay3);

            var result = paymentRepository.findByPaymentDueDateBetween(
                    LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var payment = createPayment("PAY-202501-0001", LocalDate.of(2025, 1, 31));
            paymentRepository.save(payment);

            var fetched = paymentRepository.findByPaymentNumber("PAY-202501-0001").get();
            fetched.setStatus(PaymentStatus.APPROVED);
            paymentRepository.update(fetched);

            var updated = paymentRepository.findByPaymentNumber("PAY-202501-0001").get();
            assertThat(updated.getStatus()).isEqualTo(PaymentStatus.APPROVED);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var payment = createPayment("PAY-202501-0002", LocalDate.of(2025, 1, 31));
            paymentRepository.save(payment);

            var payA = paymentRepository.findByPaymentNumber("PAY-202501-0002").get();
            var payB = paymentRepository.findByPaymentNumber("PAY-202501-0002").get();

            payA.setStatus(PaymentStatus.APPROVED);
            paymentRepository.update(payA);

            payB.setStatus(PaymentStatus.PAID);
            assertThatThrownBy(() -> paymentRepository.update(payB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var payment = createPayment("PAY-202501-0003", LocalDate.of(2025, 1, 31));
            paymentRepository.save(payment);

            var fetched = paymentRepository.findByPaymentNumber("PAY-202501-0003").get();
            paymentRepository.deleteById(fetched.getId());

            fetched.setStatus(PaymentStatus.APPROVED);
            assertThatThrownBy(() -> paymentRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で支払と支払明細を取得できる")
        void canFetchPaymentWithDetailsUsingJoin() {
            var payment = Payment.builder()
                    .paymentNumber("PAY-202501-0010")
                    .supplierCode("SUP001")
                    .paymentClosingDate(LocalDate.of(2025, 1, 31))
                    .paymentDueDate(LocalDate.of(2025, 2, 28))
                    .paymentMethod(PaymentMethod.BANK_TRANSFER)
                    .paymentAmount(new BigDecimal("150000"))
                    .taxAmount(new BigDecimal("15000"))
                    .withholdingAmount(BigDecimal.ZERO)
                    .netPaymentAmount(new BigDecimal("165000"))
                    .status(PaymentStatus.DRAFT)
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            PaymentDetail.builder()
                                    .purchaseNumber("PUR-202501-0001")
                                    .purchaseDate(LocalDate.of(2025, 1, 15))
                                    .purchaseAmount(new BigDecimal("100000"))
                                    .taxAmount(new BigDecimal("10000"))
                                    .paymentTargetAmount(new BigDecimal("110000"))
                                    .build(),
                            PaymentDetail.builder()
                                    .purchaseNumber("PUR-202501-0002")
                                    .purchaseDate(LocalDate.of(2025, 1, 20))
                                    .purchaseAmount(new BigDecimal("50000"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .paymentTargetAmount(new BigDecimal("55000"))
                                    .build()
                    ))
                    .build();
            paymentRepository.save(payment);

            var result = paymentRepository.findWithDetailsByPaymentNumber("PAY-202501-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPaymentNumber()).isEqualTo("PAY-202501-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("明細がない支払も正しく取得できる")
        void canFetchPaymentWithoutDetails() {
            var payment = createPayment("PAY-202501-0012", LocalDate.of(2025, 1, 31));
            paymentRepository.save(payment);

            var result = paymentRepository.findWithDetailsByPaymentNumber("PAY-202501-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getPaymentNumber()).isEqualTo("PAY-202501-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private Payment createPayment(String paymentNumber, LocalDate closingDate) {
        return Payment.builder()
                .paymentNumber(paymentNumber)
                .supplierCode("SUP001")
                .paymentClosingDate(closingDate)
                .paymentDueDate(closingDate.plusMonths(1))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentAmount(new BigDecimal("100000"))
                .taxAmount(new BigDecimal("10000"))
                .withholdingAmount(BigDecimal.ZERO)
                .netPaymentAmount(new BigDecimal("110000"))
                .status(PaymentStatus.DRAFT)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
