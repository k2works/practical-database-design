package com.example.sms.infrastructure.in.rest;

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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

/**
 * 顧客マスタ API テスト.
 */
@AutoConfigureMockMvc
@DisplayName("顧客マスタ API テスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class CustomerControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    private Partner createTestPartner(String code, String name) {
        return Partner.builder()
            .partnerCode(code)
            .partnerName(name)
            .partnerNameKana("テストトリヒキサキ")
            .isCustomer(true)
            .isSupplier(false)
            .postalCode("123-4567")
            .address1("東京都千代田区1-1-1")
            .address2("テストビル")
            .classificationCode(null)
            .isTradingProhibited(false)
            .isMiscellaneous(false)
            .groupCode(null)
            .creditLimit(BigDecimal.valueOf(1_000_000))
            .temporaryCreditIncrease(BigDecimal.ZERO)
            .build();
    }

    private Customer createTestCustomer(String code, String branchNumber, String name) {
        return Customer.builder()
            .customerCode(code)
            .customerBranchNumber(branchNumber)
            .customerName(name)
            .customerNameKana("テストコキャク")
            .customerCategory("01")
            .billingCode(code)
            .billingBranchNumber(branchNumber)
            .collectionCode(code)
            .collectionBranchNumber(branchNumber)
            .customerPostalCode("123-4567")
            .customerPrefecture("東京都")
            .customerAddress1("千代田区1-1-1")
            .customerAddress2("テストビル")
            .customerPhone("03-1234-5678")
            .customerFax("03-1234-5679")
            .customerEmail("test@example.com")
            .ourRepresentativeCode("REP001")
            .customerRepresentativeName("山田太郎")
            .customerDepartmentName("営業部")
            .billingType(BillingType.PERIODIC)
            .closingDay1(20)
            .paymentMonth1(1)
            .paymentDay1(10)
            .paymentMethod1(PaymentMethod.TRANSFER)
            .build();
    }

    @Nested
    @DisplayName("GET /api/v1/customers")
    class GetAllCustomers {

        @Test
        @DisplayName("顧客一覧を取得できる")
        void shouldGetAllCustomers() throws Exception {
            // Given - First create partners (due to foreign key constraint)
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先1"));
            partnerRepository.save(createTestPartner("CUS002", "テスト取引先2"));

            Customer customer1 = createTestCustomer("CUS001", "00", "テスト顧客1");
            Customer customer2 = createTestCustomer("CUS002", "00", "テスト顧客2");
            customerRepository.save(customer1);
            customerRepository.save(customer2);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerCode").value("CUS001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].customerCode").value("CUS002"));
        }

        @Test
        @DisplayName("顧客が0件の場合、空配列を返す")
        void shouldReturnEmptyListWhenNoCustomers() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/customers/{customerCode}")
    class GetCustomersByCode {

        @Test
        @DisplayName("顧客コードで顧客一覧を取得できる")
        void shouldReturnCustomersByCode() throws Exception {
            // Given
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            Customer customer1 = createTestCustomer("CUS001", "00", "テスト顧客本店");
            Customer customer2 = createTestCustomer("CUS001", "01", "テスト顧客支店");
            customerRepository.save(customer1);
            customerRepository.save(customer2);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/CUS001")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerBranchNumber").value("00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].customerBranchNumber").value("01"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/customers/{customerCode}/{branchNumber}")
    class GetCustomerByCodeAndBranch {

        @Test
        @DisplayName("顧客コードと枝番で顧客を取得できる")
        void shouldReturnCustomerByCodeAndBranch() throws Exception {
            // Given
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            Customer customer = createTestCustomer("CUS001", "00", "テスト顧客");
            customerRepository.save(customer);

            // When & Then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/CUS001/00")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerCode").value("CUS001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerBranchNumber").value("00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("テスト顧客"));
        }

        @Test
        @DisplayName("顧客が見つからない場合、404を返す")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/NOTFOUND/00")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/customers")
    class CreateCustomer {

        @Test
        @DisplayName("顧客を登録できる")
        void shouldCreateCustomer() throws Exception {
            // Given - First create partner (due to foreign key constraint)
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            String requestBody = """
                {
                    "customerCode": "CUS001",
                    "customerBranchNumber": "00",
                    "customerName": "テスト顧客",
                    "customerNameKana": "テストコキャク",
                    "customerCategory": "01",
                    "billingCode": "CUS001",
                    "billingBranchNumber": "00",
                    "collectionCode": "CUS001",
                    "collectionBranchNumber": "00",
                    "customerPostalCode": "123-4567",
                    "customerPrefecture": "東京都",
                    "customerAddress1": "千代田区1-1-1",
                    "customerPhone": "03-1234-5678",
                    "billingType": "PERIODIC",
                    "closingDay1": 20,
                    "paymentMonth1": 1,
                    "paymentDay1": 10,
                    "paymentMethod1": "TRANSFER"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerCode").value("CUS001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("テスト顧客"));
        }

        @Test
        @DisplayName("顧客が既に存在する場合、409を返す")
        void shouldReturn409WhenCustomerAlreadyExists() throws Exception {
            // Given
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            Customer existingCustomer = createTestCustomer("CUS001", "00", "既存顧客");
            customerRepository.save(existingCustomer);

            String requestBody = """
                {
                    "customerCode": "CUS001",
                    "customerBranchNumber": "00",
                    "customerName": "テスト顧客"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/customers/{customerCode}/{branchNumber}")
    class UpdateCustomer {

        @Test
        @DisplayName("顧客を更新できる")
        void shouldUpdateCustomer() throws Exception {
            // Given
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            Customer customer = createTestCustomer("CUS001", "00", "テスト顧客");
            customerRepository.save(customer);

            String requestBody = """
                {
                    "customerName": "更新後顧客名"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/CUS001/00")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerName").value("更新後顧客名"));
        }

        @Test
        @DisplayName("顧客が見つからない場合、404を返す")
        void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
            String requestBody = """
                {
                    "customerName": "更新後顧客名"
                }
                """;

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/NOTFOUND/00")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/customers/{customerCode}/{branchNumber}")
    class DeleteCustomer {

        @Test
        @DisplayName("顧客を削除できる")
        void shouldDeleteCustomer() throws Exception {
            // Given
            partnerRepository.save(createTestPartner("CUS001", "テスト取引先"));

            Customer customer = createTestCustomer("CUS001", "00", "テスト顧客");
            customerRepository.save(customer);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/CUS001/00"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        @DisplayName("顧客が見つからない場合、404を返す")
        void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/NOTFOUND/00"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }
}
