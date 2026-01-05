package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.application.port.out.InvoiceRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceDetail;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.invoice.InvoiceType;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.Partner;
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
 * 請求リポジトリテスト.
 */
@DisplayName("請求リポジトリ")
@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseUnderscoresInNumericLiterals"})
class InvoiceRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
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
        @DisplayName("請求を登録できる")
        void canRegisterInvoice() {
            var invoice = Invoice.builder()
                    .invoiceNumber("INV-2025-0001")
                    .invoiceDate(LocalDate.of(2025, 1, 31))
                    .billingCode("C001")
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .invoiceType(InvoiceType.CLOSING)
                    .closingDate(LocalDate.of(2025, 1, 31))
                    .previousBalance(BigDecimal.ZERO)
                    .receiptAmount(BigDecimal.ZERO)
                    .carriedBalance(BigDecimal.ZERO)
                    .currentSalesAmount(new BigDecimal("100000"))
                    .currentTaxAmount(new BigDecimal("10000"))
                    .currentInvoiceAmount(new BigDecimal("110000"))
                    .invoiceBalance(new BigDecimal("110000"))
                    .status(InvoiceStatus.DRAFT)
                    .dueDate(LocalDate.of(2025, 2, 28))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .build();

            invoiceRepository.save(invoice);

            var result = invoiceRepository.findByInvoiceNumber("INV-2025-0001");
            assertThat(result).isPresent();
            assertThat(result.get().getInvoiceNumber()).isEqualTo("INV-2025-0001");
            assertThat(result.get().getStatus()).isEqualTo(InvoiceStatus.DRAFT);
            assertThat(result.get().getVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("請求明細付きで登録できる")
        void canRegisterInvoiceWithDetails() {
            var invoice = Invoice.builder()
                    .invoiceNumber("INV-2025-0002")
                    .invoiceDate(LocalDate.of(2025, 1, 31))
                    .billingCode("C001")
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .invoiceType(InvoiceType.IMMEDIATE)
                    .closingDate(LocalDate.of(2025, 1, 31))
                    .currentSalesAmount(new BigDecimal("50000"))
                    .currentTaxAmount(new BigDecimal("5000"))
                    .currentInvoiceAmount(new BigDecimal("55000"))
                    .invoiceBalance(new BigDecimal("55000"))
                    .status(InvoiceStatus.DRAFT)
                    .dueDate(LocalDate.of(2025, 2, 28))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            InvoiceDetail.builder()
                                    .lineNumber(1)
                                    .salesNumber("SL-2025-0001")
                                    .salesDate(LocalDate.of(2025, 1, 15))
                                    .salesAmount(new BigDecimal("50000"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .totalAmount(new BigDecimal("55000"))
                                    .build()
                    ))
                    .build();

            invoiceRepository.save(invoice);

            var result = invoiceRepository.findWithDetailsByInvoiceNumber("INV-2025-0002");
            assertThat(result).isPresent();
            assertThat(result.get().getDetails()).hasSize(1);
            assertThat(result.get().getDetails().get(0).getSalesNumber()).isEqualTo("SL-2025-0001");
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @Test
        @DisplayName("顧客コードで検索できる")
        void canFindByCustomerCode() {
            var invoice1 = createInvoice("INV-2025-0001", LocalDate.of(2025, 1, 31));
            var invoice2 = createInvoice("INV-2025-0002", LocalDate.of(2025, 2, 28));
            invoiceRepository.save(invoice1);
            invoiceRepository.save(invoice2);

            var result = invoiceRepository.findByCustomerCode("C001");
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("ステータスで検索できる")
        void canFindByStatus() {
            var invoice1 = createInvoice("INV-2025-0001", LocalDate.of(2025, 1, 31));
            invoice1.setStatus(InvoiceStatus.ISSUED);
            var invoice2 = createInvoice("INV-2025-0002", LocalDate.of(2025, 2, 28));
            invoice2.setStatus(InvoiceStatus.DRAFT);
            invoiceRepository.save(invoice1);
            invoiceRepository.save(invoice2);

            var result = invoiceRepository.findByStatus(InvoiceStatus.ISSUED);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInvoiceNumber()).isEqualTo("INV-2025-0001");
        }

        @Test
        @DisplayName("請求日範囲で検索できる")
        void canFindByInvoiceDateBetween() {
            var invoice1 = createInvoice("INV-2025-0001", LocalDate.of(2025, 1, 15));
            var invoice2 = createInvoice("INV-2025-0002", LocalDate.of(2025, 1, 31));
            var invoice3 = createInvoice("INV-2025-0003", LocalDate.of(2025, 2, 15));
            invoiceRepository.save(invoice1);
            invoiceRepository.save(invoice2);
            invoiceRepository.save(invoice3);

            var result = invoiceRepository.findByInvoiceDateBetween(
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("楽観ロック")
    class OptimisticLocking {

        @Test
        @DisplayName("同じバージョンで更新できる")
        void canUpdateWithSameVersion() {
            var invoice = createInvoice("INV-2025-0001", LocalDate.of(2025, 1, 31));
            invoiceRepository.save(invoice);

            var fetched = invoiceRepository.findByInvoiceNumber("INV-2025-0001").get();
            fetched.setStatus(InvoiceStatus.ISSUED);
            invoiceRepository.update(fetched);

            var updated = invoiceRepository.findByInvoiceNumber("INV-2025-0001").get();
            assertThat(updated.getStatus()).isEqualTo(InvoiceStatus.ISSUED);
            assertThat(updated.getVersion()).isEqualTo(2);
        }

        @Test
        @DisplayName("異なるバージョンで更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenVersionMismatch() {
            var invoice = createInvoice("INV-2025-0002", LocalDate.of(2025, 1, 31));
            invoiceRepository.save(invoice);

            var invoiceA = invoiceRepository.findByInvoiceNumber("INV-2025-0002").get();
            var invoiceB = invoiceRepository.findByInvoiceNumber("INV-2025-0002").get();

            invoiceA.setStatus(InvoiceStatus.ISSUED);
            invoiceRepository.update(invoiceA);

            invoiceB.setStatus(InvoiceStatus.PARTIALLY_PAID);
            assertThatThrownBy(() -> invoiceRepository.update(invoiceB))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("他のユーザーによって更新されています");
        }

        @Test
        @DisplayName("削除されたエンティティを更新すると楽観ロック例外が発生する")
        void throwsExceptionWhenEntityDeleted() {
            var invoice = createInvoice("INV-2025-0003", LocalDate.of(2025, 1, 31));
            invoiceRepository.save(invoice);

            var fetched = invoiceRepository.findByInvoiceNumber("INV-2025-0003").get();
            invoiceRepository.deleteById(fetched.getId());

            fetched.setStatus(InvoiceStatus.ISSUED);
            assertThatThrownBy(() -> invoiceRepository.update(fetched))
                    .isInstanceOf(OptimisticLockException.class)
                    .hasMessageContaining("既に削除されています");
        }
    }

    @Nested
    @DisplayName("リレーション設定（ネストResultMap）")
    class NestedResultMap {

        @Test
        @DisplayName("JOINによる一括取得で請求と請求明細を取得できる")
        void canFetchInvoiceWithDetailsUsingJoin() {
            var invoice = Invoice.builder()
                    .invoiceNumber("INV-2025-0010")
                    .invoiceDate(LocalDate.of(2025, 1, 31))
                    .billingCode("C001")
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .invoiceType(InvoiceType.IMMEDIATE)
                    .closingDate(LocalDate.of(2025, 1, 31))
                    .currentSalesAmount(new BigDecimal("100000"))
                    .currentTaxAmount(new BigDecimal("10000"))
                    .currentInvoiceAmount(new BigDecimal("110000"))
                    .invoiceBalance(new BigDecimal("110000"))
                    .status(InvoiceStatus.DRAFT)
                    .dueDate(LocalDate.of(2025, 2, 28))
                    .createdBy("test-user")
                    .updatedBy("test-user")
                    .details(List.of(
                            InvoiceDetail.builder()
                                    .lineNumber(1)
                                    .salesNumber("SL-2025-0001")
                                    .salesDate(LocalDate.of(2025, 1, 15))
                                    .salesAmount(new BigDecimal("50000"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .totalAmount(new BigDecimal("55000"))
                                    .build(),
                            InvoiceDetail.builder()
                                    .lineNumber(2)
                                    .salesNumber("SL-2025-0002")
                                    .salesDate(LocalDate.of(2025, 1, 20))
                                    .salesAmount(new BigDecimal("50000"))
                                    .taxAmount(new BigDecimal("5000"))
                                    .totalAmount(new BigDecimal("55000"))
                                    .build()
                    ))
                    .build();
            invoiceRepository.save(invoice);

            var result = invoiceRepository.findWithDetailsByInvoiceNumber("INV-2025-0010");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getInvoiceNumber()).isEqualTo("INV-2025-0010");
            assertThat(fetched.getVersion()).isEqualTo(1);
            assertThat(fetched.getDetails()).hasSize(2);

            var detail1 = fetched.getDetails().get(0);
            assertThat(detail1.getLineNumber()).isEqualTo(1);
            assertThat(detail1.getSalesNumber()).isEqualTo("SL-2025-0001");

            var detail2 = fetched.getDetails().get(1);
            assertThat(detail2.getLineNumber()).isEqualTo(2);
            assertThat(detail2.getSalesNumber()).isEqualTo("SL-2025-0002");
        }

        @Test
        @DisplayName("明細がない請求も正しく取得できる")
        void canFetchInvoiceWithoutDetails() {
            var invoice = createInvoice("INV-2025-0012", LocalDate.of(2025, 1, 31));
            invoiceRepository.save(invoice);

            var result = invoiceRepository.findWithDetailsByInvoiceNumber("INV-2025-0012");

            assertThat(result).isPresent();
            var fetched = result.get();
            assertThat(fetched.getInvoiceNumber()).isEqualTo("INV-2025-0012");
            assertThat(fetched.getDetails()).isEmpty();
        }
    }

    private Invoice createInvoice(String invoiceNumber, LocalDate invoiceDate) {
        return Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(invoiceDate)
                .billingCode("C001")
                .customerCode("C001")
                .customerBranchNumber("00")
                .invoiceType(InvoiceType.CLOSING)
                .closingDate(invoiceDate)
                .previousBalance(BigDecimal.ZERO)
                .receiptAmount(BigDecimal.ZERO)
                .carriedBalance(BigDecimal.ZERO)
                .currentSalesAmount(new BigDecimal("100000"))
                .currentTaxAmount(new BigDecimal("10000"))
                .currentInvoiceAmount(new BigDecimal("110000"))
                .invoiceBalance(new BigDecimal("110000"))
                .status(InvoiceStatus.DRAFT)
                .dueDate(invoiceDate.plusMonths(1))
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }
}
