package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.CustomerRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.partner.PaymentMethod;
import com.example.sms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 取引先リポジトリテスト.
 */
@DisplayName("取引先リポジトリ")
class PartnerRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Nested
    @DisplayName("取引先登録")
    class PartnerRegistration {

        @Test
        @DisplayName("取引先を登録できる")
        void canRegisterPartner() {
            // Arrange
            var partner = Partner.builder()
                    .partnerCode("P001")
                    .partnerName("テスト取引先")
                    .partnerNameKana("テストトリヒキサキ")
                    .isCustomer(true)
                    .isSupplier(false)
                    .postalCode("100-0001")
                    .address1("東京都千代田区")
                    .creditLimit(new BigDecimal("1000000"))
                    .build();

            // Act
            partnerRepository.save(partner);

            // Assert
            var result = partnerRepository.findByCode("P001");
            assertThat(result).isPresent();
            assertThat(result.get().getPartnerName()).isEqualTo("テスト取引先");
            assertThat(result.get().isCustomer()).isTrue();
            assertThat(result.get().getCreditLimit()).isEqualByComparingTo(new BigDecimal("1000000"));
        }

        @Test
        @DisplayName("顧客と仕入先の両方を持つ取引先を登録できる")
        void canRegisterBothCustomerAndSupplier() {
            // Arrange
            var partner = Partner.builder()
                    .partnerCode("P002")
                    .partnerName("顧客兼仕入先")
                    .isCustomer(true)
                    .isSupplier(true)
                    .build();

            // Act
            partnerRepository.save(partner);

            // Assert
            var result = partnerRepository.findByCode("P002");
            assertThat(result).isPresent();
            assertThat(result.get().isCustomer()).isTrue();
            assertThat(result.get().isSupplier()).isTrue();
        }
    }

    @Nested
    @DisplayName("取引先検索")
    class PartnerSearch {

        @Test
        @DisplayName("顧客のみを検索できる")
        void canFindCustomersOnly() {
            // Arrange
            var customer = Partner.builder()
                    .partnerCode("C001")
                    .partnerName("顧客A")
                    .isCustomer(true)
                    .isSupplier(false)
                    .build();
            partnerRepository.save(customer);

            var supplier = Partner.builder()
                    .partnerCode("S001")
                    .partnerName("仕入先A")
                    .isCustomer(false)
                    .isSupplier(true)
                    .build();
            partnerRepository.save(supplier);

            // Act
            var customers = partnerRepository.findCustomers();

            // Assert
            assertThat(customers).hasSize(1);
            assertThat(customers.get(0).getPartnerName()).isEqualTo("顧客A");
        }

        @Test
        @DisplayName("仕入先のみを検索できる")
        void canFindSuppliersOnly() {
            // Arrange
            var customer = Partner.builder()
                    .partnerCode("C001")
                    .partnerName("顧客A")
                    .isCustomer(true)
                    .isSupplier(false)
                    .build();
            partnerRepository.save(customer);

            var supplier = Partner.builder()
                    .partnerCode("S001")
                    .partnerName("仕入先A")
                    .isCustomer(false)
                    .isSupplier(true)
                    .build();
            partnerRepository.save(supplier);

            // Act
            var suppliers = partnerRepository.findSuppliers();

            // Assert
            assertThat(suppliers).hasSize(1);
            assertThat(suppliers.get(0).getPartnerName()).isEqualTo("仕入先A");
        }
    }

    @Nested
    @DisplayName("顧客登録")
    class CustomerRegistration {

        @Test
        @DisplayName("顧客詳細を登録できる")
        void canRegisterCustomerDetails() {
            // Arrange: 取引先を先に登録
            var partner = Partner.builder()
                    .partnerCode("C001")
                    .partnerName("顧客A")
                    .isCustomer(true)
                    .build();
            partnerRepository.save(partner);

            // Arrange: 顧客詳細
            var customer = Customer.builder()
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .customerName("顧客A本社")
                    .billingType(BillingType.PERIODIC)
                    .closingDay1(20)
                    .paymentMonth1(1)
                    .paymentDay1(10)
                    .paymentMethod1(PaymentMethod.TRANSFER)
                    .build();

            // Act
            customerRepository.save(customer);

            // Assert
            var result = customerRepository.findByCodeAndBranch("C001", "00");
            assertThat(result).isPresent();
            assertThat(result.get().getCustomerName()).isEqualTo("顧客A本社");
            assertThat(result.get().getBillingType()).isEqualTo(BillingType.PERIODIC);
            assertThat(result.get().getClosingDay1()).isEqualTo(20);
            assertThat(result.get().getPaymentMethod1()).isEqualTo(PaymentMethod.TRANSFER);
        }

        @Test
        @DisplayName("同じ取引先に複数の顧客枝番を登録できる")
        void canRegisterMultipleBranches() {
            // Arrange: 取引先を先に登録
            var partner = Partner.builder()
                    .partnerCode("C001")
                    .partnerName("顧客A")
                    .isCustomer(true)
                    .build();
            partnerRepository.save(partner);

            // Arrange: 本社
            var headquarters = Customer.builder()
                    .customerCode("C001")
                    .customerBranchNumber("00")
                    .customerName("顧客A本社")
                    .build();
            customerRepository.save(headquarters);

            // Arrange: 支店
            var branch = Customer.builder()
                    .customerCode("C001")
                    .customerBranchNumber("01")
                    .customerName("顧客A東京支店")
                    .billingCode("C001")
                    .billingBranchNumber("00")
                    .build();
            customerRepository.save(branch);

            // Act
            var customers = customerRepository.findByCode("C001");

            // Assert
            assertThat(customers).hasSize(2);
            assertThat(customers)
                    .extracting(Customer::getCustomerName)
                    .containsExactlyInAnyOrder("顧客A本社", "顧客A東京支店");
        }
    }
}
