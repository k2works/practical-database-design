package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * 受注画面コントローラーテスト.
 */
@WebMvcTest(OrderWebController.class)
@DisplayName("受注画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class OrderWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @MockitoBean
    private PartnerUseCase partnerUseCase;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Nested
    @DisplayName("GET /orders")
    class ListOrders {

        @Test
        @DisplayName("受注一覧画面を表示できる")
        void shouldDisplayOrderList() throws Exception {
            SalesOrder order = createTestOrder("ORD-WEB-001");
            when(orderUseCase.getAllOrders()).thenReturn(List.of(order));
            when(partnerUseCase.getCustomers()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("orders"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("statuses"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("customers"));
        }

        @Test
        @DisplayName("ステータスでフィルタできる")
        void shouldFilterByStatus() throws Exception {
            SalesOrder order = createTestOrder("ORD-WEB-002");
            when(orderUseCase.getOrdersByStatus(OrderStatus.RECEIVED)).thenReturn(List.of(order));
            when(partnerUseCase.getCustomers()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                    .param("status", "RECEIVED"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedStatus", OrderStatus.RECEIVED));
        }
    }

    @Nested
    @DisplayName("GET /orders/{orderNumber}")
    class ShowOrder {

        @Test
        @DisplayName("受注詳細画面を表示できる")
        void shouldDisplayOrderDetail() throws Exception {
            SalesOrder order = createTestOrder("ORD-WEB-004");
            when(orderUseCase.getOrderWithDetails("ORD-WEB-004")).thenReturn(order);

            mockMvc.perform(MockMvcRequestBuilders.get("/orders/ORD-WEB-004"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("order"));
        }
    }

    @Nested
    @DisplayName("GET /orders/new")
    class NewOrderForm {

        @Test
        @DisplayName("受注登録フォームを表示できる")
        void shouldDisplayNewOrderForm() throws Exception {
            when(partnerUseCase.getCustomers()).thenReturn(List.of());
            when(productUseCase.getAllProducts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/orders/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("customers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"));
        }
    }

    @Nested
    @DisplayName("POST /orders")
    class CreateOrder {

        @Test
        @DisplayName("受注を登録できる")
        void shouldCreateOrder() throws Exception {
            SalesOrder created = createTestOrder("ORD-NEW-001");
            when(orderUseCase.createOrder(any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("orderDate", LocalDate.now().toString())
                    .param("customerCode", "CUST-001")
                    .param("customerBranchNumber", "00")
                    .param("details[0].productCode", "PROD-001")
                    .param("details[0].productName", "テスト商品")
                    .param("details[0].orderQuantity", "10")
                    .param("details[0].unit", "個")
                    .param("details[0].unitPrice", "5000"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /orders/{orderNumber}/cancel")
    class CancelOrder {

        @Test
        @DisplayName("受注をキャンセルできる")
        void shouldCancelOrder() throws Exception {
            SalesOrder cancelled = createTestOrder("ORD-WEB-CANCEL-001");
            when(orderUseCase.cancelOrder(anyString(), anyInt())).thenReturn(cancelled);

            mockMvc.perform(MockMvcRequestBuilders.post("/orders/ORD-WEB-CANCEL-001/cancel")
                    .param("version", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders/ORD-WEB-CANCEL-001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /orders/{orderNumber}/delete")
    class DeleteOrder {

        @Test
        @DisplayName("受注を削除できる")
        void shouldDeleteOrder() throws Exception {
            doNothing().when(orderUseCase).deleteOrder("ORD-WEB-DEL-001");

            mockMvc.perform(MockMvcRequestBuilders.post("/orders/ORD-WEB-DEL-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("GET /orders/add-detail-row")
    class AddDetailRow {

        @Test
        @DisplayName("明細行フラグメントを取得できる")
        void shouldGetDetailRowFragment() throws Exception {
            when(productUseCase.getAllProducts()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/orders/add-detail-row")
                    .param("index", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("orders/fragments :: detailRow"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"));
        }
    }

    @Nested
    @DisplayName("GET /orders/product-info")
    class GetProductInfo {

        @Test
        @DisplayName("商品情報をJSONで取得できる")
        void shouldGetProductInfoAsJson() throws Exception {
            Product product = createTestProduct("PROD-001", "テスト商品");
            when(productUseCase.getProductByCode("PROD-001")).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.get("/orders/product-info")
                    .param("productCode", "PROD-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productCode").value("PROD-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("テスト商品"));
        }
    }

    private SalesOrder createTestOrder(String orderNumber) {
        SalesOrderDetail detail = SalesOrderDetail.builder()
            .productCode("PROD-001")
            .productName("テスト商品")
            .orderQuantity(new BigDecimal("10"))
            .unit("個")
            .unitPrice(new BigDecimal("5000"))
            .build();

        return SalesOrder.builder()
            .orderNumber(orderNumber)
            .orderDate(LocalDate.now())
            .customerCode("CUST-001")
            .customerBranchNumber("00")
            .status(OrderStatus.RECEIVED)
            .orderAmount(new BigDecimal("50000"))
            .taxAmount(new BigDecimal("5000"))
            .totalAmount(new BigDecimal("55000"))
            .version(1)
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
