package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentDetail;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 出荷画面コントローラーテスト.
 */
@WebMvcTest(ShipmentWebController.class)
@DisplayName("出荷画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ShipmentWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShipmentUseCase shipmentUseCase;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @MockitoBean
    private PartnerUseCase partnerUseCase;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Nested
    @DisplayName("GET /shipments")
    class ListShipments {

        @Test
        @DisplayName("出荷一覧画面を表示できる")
        void shouldDisplayShipmentList() throws Exception {
            Shipment shipment = createTestShipment("SHIP-WEB-001");
            PageResult<Shipment> pageResult = new PageResult<>(
                List.of(shipment), 0, 10, 1L);
            Mockito.when(shipmentUseCase.getShipments(
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull())).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("shipments"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("statuses"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentSize"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Shipment shipment = createTestShipment("SHIP-WEB-002");
            PageResult<Shipment> pageResult = new PageResult<>(
                List.of(shipment), 0, 10, 1L);
            Mockito.when(shipmentUseCase.getShipments(
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyString())).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments")
                    .param("keyword", "SHIP-WEB"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "SHIP-WEB"));
        }

        @Test
        @DisplayName("ページネーションができる")
        void shouldPaginate() throws Exception {
            Shipment shipment = createTestShipment("SHIP-WEB-003");
            PageResult<Shipment> pageResult = new PageResult<>(
                List.of(shipment), 1, 25, 30L);
            Mockito.when(shipmentUseCase.getShipments(
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull())).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments")
                    .param("page", "1")
                    .param("size", "25"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 25));
        }
    }

    @Nested
    @DisplayName("GET /shipments/{shipmentNumber}")
    class ShowShipment {

        @Test
        @DisplayName("出荷詳細画面を表示できる")
        void shouldDisplayShipmentDetail() throws Exception {
            Shipment shipment = createTestShipment("SHIP-WEB-003");
            Mockito.when(shipmentUseCase.getShipmentByNumber("SHIP-WEB-003")).thenReturn(shipment);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments/SHIP-WEB-003"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("shipment"));
        }
    }

    @Nested
    @DisplayName("GET /shipments/new")
    class NewShipmentForm {

        @Test
        @DisplayName("出荷登録フォームを表示できる")
        void shouldDisplayNewShipmentForm() throws Exception {
            Mockito.when(partnerUseCase.getCustomers()).thenReturn(List.of());
            Mockito.when(productUseCase.getAllProducts()).thenReturn(List.of());
            Mockito.when(orderUseCase.getAllOrders()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("customers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("orders"));
        }
    }

    @Nested
    @DisplayName("POST /shipments")
    class CreateShipment {

        @Test
        @DisplayName("出荷を登録できる")
        void shouldCreateShipment() throws Exception {
            Shipment created = createTestShipment("SHIP-NEW-001");
            Mockito.when(shipmentUseCase.createShipment(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/shipments")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("shipmentDate", LocalDate.now().toString())
                    .param("customerCode", "CUST-001")
                    .param("customerBranchNumber", "00")
                    .param("details[0].productCode", "PROD-001")
                    .param("details[0].productName", "テスト商品")
                    .param("details[0].shippedQuantity", "10")
                    .param("details[0].unit", "個")
                    .param("details[0].unitPrice", "5000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipments"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /shipments/{shipmentNumber}/confirm")
    class ConfirmShipment {

        @Test
        @DisplayName("出荷を確定できる")
        void shouldConfirmShipment() throws Exception {
            Shipment confirmed = createTestShipment("SHIP-WEB-CONFIRM-001");
            Mockito.when(shipmentUseCase.confirmShipment(ArgumentMatchers.anyString())).thenReturn(confirmed);

            mockMvc.perform(MockMvcRequestBuilders.post("/shipments/SHIP-WEB-CONFIRM-001/confirm"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipments/SHIP-WEB-CONFIRM-001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /shipments/{shipmentNumber}/delete")
    class DeleteShipment {

        @Test
        @DisplayName("出荷を削除できる")
        void shouldDeleteShipment() throws Exception {
            Mockito.doNothing().when(shipmentUseCase).deleteShipment("SHIP-WEB-DEL-001");

            mockMvc.perform(MockMvcRequestBuilders.post("/shipments/SHIP-WEB-DEL-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipments"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("GET /shipments/add-detail-row")
    class AddDetailRow {

        @Test
        @DisplayName("明細行フラグメントを取得できる")
        void shouldGetDetailRowFragment() throws Exception {
            Mockito.when(productUseCase.getAllProducts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments/add-detail-row")
                    .param("index", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipments/fragments :: detailRow"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"));
        }
    }

    @Nested
    @DisplayName("GET /shipments/product-info")
    class GetProductInfo {

        @Test
        @DisplayName("商品情報をJSONで取得できる")
        void shouldGetProductInfoAsJson() throws Exception {
            Product product = createTestProduct("PROD-001", "テスト商品");
            Mockito.when(productUseCase.getProductByCode("PROD-001")).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipments/product-info")
                    .param("productCode", "PROD-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCode").value("PROD-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("テスト商品"));
        }
    }

    private Shipment createTestShipment(String shipmentNumber) {
        ShipmentDetail detail = ShipmentDetail.builder()
            .productCode("PROD-001")
            .productName("テスト商品")
            .shippedQuantity(BigDecimal.TEN)
            .unit("個")
            .unitPrice(new BigDecimal("5000"))
            .amount(new BigDecimal("50000"))
            .build();

        return Shipment.builder()
            .shipmentNumber(shipmentNumber)
            .shipmentDate(LocalDate.now())
            .customerCode("CUST-001")
            .customerBranchNumber("00")
            .status(ShipmentStatus.INSTRUCTED)
            .details(List.of(detail))
            .build();
    }

    private Product createTestProduct(String productCode, String productName) {
        return Product.builder()
            .productCode(productCode)
            .productFullName(productName + " フルネーム")
            .productName(productName)
            .productNameKana("テストショウヒン")
            .productCategory(ProductCategory.PRODUCT)
            .modelNumber("MODEL-001")
            .sellingPrice(new BigDecimal("5000"))
            .purchasePrice(new BigDecimal("3000"))
            .taxCategory(TaxCategory.EXCLUSIVE)
            .isMiscellaneous(false)
            .isInventoryManaged(true)
            .isInventoryAllocated(true)
            .build();
    }
}
