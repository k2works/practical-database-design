package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ReceiptRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptApplication;
import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 入金リポジトリテスト.
 */
@DisplayName("入金リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class ReceiptRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        receiptRepository.deleteAll();
        customerRepository.deleteAll();
        partnerRepository.deleteAll();

        var partner = Partner.builder()
                .partnerCode("C001")
                .partnerName("テスト顧客")
                .isCustomer(true)
                .build();
        partnerRepository.save(partner);

        var customer = Customer.builder()
                .customerCode("C001")
                .customerBranchNumber("00")
                .customerName("テスト顧客本社")
                .build();
        customerRepository.save(customer);
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("入金を登録できる")
        void canRegisterReceipt() {
            var receipt = Receipt.builder()
                    .receiptNumber("RCP-2025-0001")
                    .receiptDate(LocalDate.of(2025, 2, 15))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .receiptMethod(ReceiptMethod.BANK_TRANSFER)
                    .receiptAmount(new BigDecimal("110000"))
                    .appliedAmount(BigDecimal.ZERO)
                    .unappliedAmount(new BigDecimal("110000"))
                    .status(ReceiptStatus.RECEIVED)
                    .payerName("テスト顧客")
                    .bankName("テスト銀行")
                    .accountNumber("1234567")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            receiptRepository.save(receipt);

            var result = receiptRepository.findByReceiptNumber("RCP-2025-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getReceiptNumber()).isEqualTo("RCP-2025-0001");
            assertThat(result.get().getStatus()).isEqualTo(ReceiptStatus.RECEIVED);
            assertThat(result.get().getReceiptMethod()).isEqualTo(ReceiptMethod.BANK_TRANSFER);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("全ての入金方法で登録できる")
        void canRegisterAllReceiptMethods() {
            var methods = ReceiptMethod.values();

            for (int i = 0; i < methods.length; i++) {
                var receipt = createReceipt("RCP-2025-" + String.format("%04d", i + 1),
                        LocalDate.of(2025, 2, 15));
                receipt.setReceiptMethod(methods[i]);
                receiptRepository.save(receipt);

                var result = receiptRepository.findByReceiptNumber(receipt.getReceiptNumber());
                assertThat(result).isPresent();
                assertThat(result.get().getReceiptMethod()).isEqualTo(methods[i]);
            }
        }

        @Test
        @DisplayName("入金消込明細付きで登録できる")
        void canRegisterReceiptWithApplications() {
            var receipt = Receipt.builder()
                    .receiptNumber("RCP-2025-0010")
                    .receiptDate(LocalDate.of(2025, 2, 15))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .receiptMethod(ReceiptMethod.BANK_TRANSFER)
                    .receiptAmount(new BigDecimal("110000"))
                    .appliedAmount(new BigDecimal("110000"))
                    .unappliedAmount(BigDecimal.ZERO)
                    .status(ReceiptStatus.APPLIED)
                    .payerName("テスト顧客")
                    .bankName("テスト銀行")
                    .accountNumber("1234567")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .applications(List.of(
                            ReceiptApplication.builder()
                                    .lineNumber(1)
                                    .applicationDate(LocalDate.of(2025, 2, 15))
                                    .appliedAmount(new BigDecimal("110000"))
                                    .remarks("消込テスト")
                                    .build()
                    ))
                    .build();

            receiptRepository.save(receipt);

            var result = receiptRepository.findWithApplicationsByReceiptNumber("RCP-2025-0010");
            assertThat(result).isPresent();
            assertThat(result.get().getApplications()).hasSize(1);
            assertThat(result.get().getApplications().get(0).getAppliedAmount())
                    .isEqualByComparingTo(new BigDecimal("110000"));
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("顧客コードで検索できる")
        void canFindByCustomerCode() {
            var receipt1 = createReceipt("RCP-2025-0001", LocalDate.of(2025, 2, 15));
            var receipt2 = createReceipt("RCP-2025-0002", LocalDate.of(2025, 2, 28));
            receiptRepository.save(receipt1);
            receiptRepository.save(receipt2);

            var result = receiptRepository.findByCustomerCode("C001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var receipt1 = createReceipt("RCP-2025-0001", LocalDate.of(2025, 2, 15));
            receipt1.setStatus(ReceiptStatus.APPLIED);
            var receipt2 = createReceipt("RCP-2025-0002", LocalDate.of(2025, 2, 28));
            receipt2.setStatus(ReceiptStatus.RECEIVED);
            receiptRepository.save(receipt1);
            receiptRepository.save(receipt2);

            var result = receiptRepository.findByStatus(ReceiptStatus.APPLIED);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReceiptNumber()).isEqualTo("RCP-2025-0001");
        }

        @Test
        @DisplayName("入金日範囲で検索できる")
        void canFindByReceiptDateBetween() {
            var receipt1 = createReceipt("RCP-2025-0001", LocalDate.of(2025, 2, 10));
            var receipt2 = createReceipt("RCP-2025-0002", LocalDate.of(2025, 2, 15));
            var receipt3 = createReceipt("RCP-2025-0003", LocalDate.of(2025, 3, 10));
            receiptRepository.save(receipt1);
            receiptRepository.save(receipt2);
            receiptRepository.save(receipt3);

            var result = receiptRepository.findByReceiptDateBetween(
                    LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("顧客と期間で入金合計を取得できる")
        void canSumReceiptsByCustomerAndDateRange() {
            var receipt1 = createReceipt("RCP-2025-0001", LocalDate.of(2025, 2, 10));
            receipt1.setReceiptAmount(new BigDecimal("100000"));
            var receipt2 = createReceipt("RCP-2025-0002", LocalDate.of(2025, 2, 15));
            receipt2.setReceiptAmount(new BigDecimal("50000"));
            receiptRepository.save(receipt1);
            receiptRepository.save(receipt2);

            var result = receiptRepository.sumReceiptsByCustomerAndDateRange(
                    "C001", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28));
            assertThat(result).isEqualByComparingTo(new BigDecimal("150000"));
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var receipt = createReceipt("RCP-2025-0001", LocalDate.of(2025, 2, 15));
            receiptRepository.save(receipt);

            var fetched = receiptRepository.findByReceiptNumber("RCP-2025-0001").get();
            fetched.setStatus(ReceiptStatus.PARTIALLY_APPLIED);
            fetched.setAppliedAmount(new BigDecimal("50000"));
            fetched.setUnappliedAmount(new BigDecimal("60000"));
            receiptRepository.update(fetched);

            var updated = receiptRepository.findByReceiptNumber("RCP-2025-0001").get();
            assertThat(updated.getStatus()).isEqualTo(ReceiptStatus.PARTIALLY_APPLIED);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var receipt = createReceipt("RCP-2025-0002", LocalDate.of(2025, 2, 15));
            receiptRepository.save(receipt);

            var receiptA = receiptRepository.findByReceiptNumber("RCP-2025-0002").get();
            var receiptB = receiptRepository.findByReceiptNumber("RCP-2025-0002").get();

            receiptA.setStatus(ReceiptStatus.PARTIALLY_APPLIED);
            receiptRepository.update(receiptA);

            receiptB.setStatus(ReceiptStatus.APPLIED);
            assertThatThrownBy(() -> receiptRepository.update(receiptB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var receipt = createReceipt("RCP-2025-0003", LocalDate.of(2025, 2, 15));
            receiptRepository.save(receipt);

            var fetched = receiptRepository.findByReceiptNumber("RCP-2025-0003").get();
            receiptRepository.deleteById(fetched.getId());

            fetched.setStatus(ReceiptStatus.APPLIED);
            assertThatThrownBy(() -> receiptRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で入金と消込明細を取得できる")
        void canFetchReceiptWithApplicationsUsingJoin() {
            var receipt = Receipt.builder()
                    .receiptNumber("RCP-2025-0020")
                    .receiptDate(LocalDate.of(2025, 2, 15))
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .receiptMethod(ReceiptMethod.BANK_TRANSFER)
                    .receiptAmount(new BigDecimal("200000"))
                    .appliedAmount(new BigDecimal("200000"))
                    .unappliedAmount(BigDecimal.ZERO)
                    .status(ReceiptStatus.APPLIED)
                    .payerName("テスト顧客")
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .applications(List.of(
                            ReceiptApplication.builder()
                                    .lineNumber(1)
                                    .applicationDate(LocalDate.of(2025, 2, 15))
                                    .appliedAmount(new BigDecimal("110000"))
                                    .remarks("消込1")
                                    .build(),
                            ReceiptApplication.builder()
                                    .lineNumber(2)
                                    .applicationDate(LocalDate.of(2025, 2, 15))
                                    .appliedAmount(new BigDecimal("90000"))
                                    .remarks("消込2")
                                    .build()
                    ))
                    .build();
            receiptRepository.save(receipt);

            var result = receiptRepository.findWithApplicationsByReceiptNumber("RCP-2025-0020");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getReceiptNumber()).isEqualTo("RCP-2025-0020");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getApplications()).hasSize(2);

            var app1 = fetched.getApplications().get(0);
            assertThat(app1.getLineNumber()).isEqualTo(1);
            assertThat(app1.getAppliedAmount()).isEqualByComparingTo(new BigDecimal("110000"));

            var app2 = fetched.getApplications().get(1);
            assertThat(app2.getLineNumber()).isEqualTo(2);
            assertThat(app2.getAppliedAmount()).isEqualByComparingTo(new BigDecimal("90000"));
        }

        @Test
        @DisplayName("消込明細がない入金も正しく取得できる")
        void canFetchReceiptWithoutApplications() {
            var receipt = createReceipt("RCP-2025-0030", LocalDate.of(2025, 2, 15));
            receiptRepository.save(receipt);

            var result = receiptRepository.findWithApplicationsByReceiptNumber("RCP-2025-0030");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getReceiptNumber()).isEqualTo("RCP-2025-0030");
            assertThat(fetched.getApplications()).isEmpty();
        }
    }

    private Receipt createReceipt(String receiptNumber, LocalDate receiptDate) {
        return Receipt.builder()
                .receiptNumber(receiptNumber)
                .receiptDate(receiptDate)
                .customerCode("C001")
                .customerBranchNumber("00")
                .receiptMethod(ReceiptMethod.BANK_TRANSFER)
                .receiptAmount(new BigDecimal("110000"))
                .appliedAmount(BigDecimal.ZERO)
                .unappliedAmount(new BigDecimal("110000"))
                .status(ReceiptStatus.RECEIVED)
                .payerName("テスト顧客")
                .bankName("テスト銀行")
                .accountNumber("1234567")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
