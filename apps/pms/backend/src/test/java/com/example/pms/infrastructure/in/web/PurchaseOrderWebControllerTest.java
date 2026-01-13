package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.infrastructure.report.ExcelReportGenerator;
import com.example.pms.infrastructure.report.PdfReportGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 発注業務画面コントローラーテスト.
 */
@WebMvcTest(PurchaseOrderWebController.class)
@DisplayName("発注業務画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class PurchaseOrderWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseOrderUseCase purchaseOrderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private SupplierRepository supplierRepository;

    @MockitoBean
    private ExcelReportGenerator excelReportGenerator;

    @MockitoBean
    private PdfReportGenerator pdfReportGenerator;

    @Nested
    @DisplayName("GET /purchase-orders - 発注一覧")
    class ListOrders {

        @Test
        @DisplayName("発注一覧画面を表示できる")
        void shouldDisplayOrderList() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.CREATING);
            Mockito.when(purchaseOrderUseCase.getAllOrders()).thenReturn(List.of(order));

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("purchase-orders/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("orders"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("statuses"));
        }

        @Test
        @DisplayName("ステータスでフィルタできる")
        void shouldFilterByStatus() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.ORDERED);
            Mockito.when(purchaseOrderUseCase.getOrdersByStatus(PurchaseOrderStatus.ORDERED))
                .thenReturn(List.of(order));

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders")
                    .param("status", "ORDERED"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("purchase-orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedStatus", PurchaseOrderStatus.ORDERED));
        }
    }

    @Nested
    @DisplayName("GET /purchase-orders/{orderNumber} - 発注詳細")
    class ShowOrder {

        @Test
        @DisplayName("発注詳細画面を表示できる")
        void shouldDisplayOrderDetail() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.CREATING);
            Mockito.when(purchaseOrderUseCase.getOrder("PO-001")).thenReturn(order);

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders/PO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("purchase-orders/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("order"));
        }
    }

    @Nested
    @DisplayName("GET /purchase-orders/new - 発注登録画面")
    class NewOrder {

        @Test
        @DisplayName("発注登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            Mockito.when(supplierRepository.findAll()).thenReturn(List.of(createTestSupplier()));
            Mockito.when(itemUseCase.getItemsByCategory(ItemCategory.MATERIAL))
                .thenReturn(List.of(createTestItem()));

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("purchase-orders/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("suppliers"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"));
        }
    }

    @Nested
    @DisplayName("POST /purchase-orders/{orderNumber}/confirm - 発注確定")
    class ConfirmOrder {

        @Test
        @DisplayName("発注を確定できる")
        void shouldConfirmOrder() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.ORDERED);
            Mockito.when(purchaseOrderUseCase.confirmOrder("PO-001")).thenReturn(order);

            mockMvc.perform(MockMvcRequestBuilders.post("/purchase-orders/PO-001/confirm"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/purchase-orders/PO-001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /purchase-orders/{orderNumber}/cancel - 発注取消")
    class CancelOrder {

        @Test
        @DisplayName("発注を取消できる")
        void shouldCancelOrder() throws Exception {
            Mockito.doNothing().when(purchaseOrderUseCase).cancelOrder("PO-001");

            mockMvc.perform(MockMvcRequestBuilders.post("/purchase-orders/PO-001/cancel"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/purchase-orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("GET /purchase-orders/export - 帳票出力")
    class ExportOrders {

        @Test
        @DisplayName("Excel で出力できる")
        void shouldExportExcel() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.CREATING);
            Mockito.when(purchaseOrderUseCase.getAllOrders()).thenReturn(List.of(order));
            Mockito.when(excelReportGenerator.generatePurchaseOrderList(ArgumentMatchers.anyList()))
                .thenReturn(new byte[]{1, 2, 3});

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders/export/excel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
        }

        @Test
        @DisplayName("PDF で出力できる")
        void shouldExportPdf() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.CREATING);
            Mockito.when(purchaseOrderUseCase.getAllOrders()).thenReturn(List.of(order));
            Mockito.when(pdfReportGenerator.generatePurchaseOrderList(ArgumentMatchers.anyList()))
                .thenReturn(new byte[]{1, 2, 3});

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders/export/pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
        }

        @Test
        @DisplayName("発注書 PDF を出力できる")
        void shouldExportOrderPdf() throws Exception {
            PurchaseOrder order = createTestOrder("PO-001", PurchaseOrderStatus.ORDERED);
            Mockito.when(purchaseOrderUseCase.getOrder("PO-001")).thenReturn(order);
            Mockito.when(pdfReportGenerator.generatePurchaseOrderPdf(ArgumentMatchers.any(PurchaseOrder.class)))
                .thenReturn(new byte[]{1, 2, 3});

            mockMvc.perform(MockMvcRequestBuilders.get("/purchase-orders/PO-001/pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"))
                .andExpect(MockMvcResultMatchers.content().contentType("application/pdf"));
        }
    }

    private PurchaseOrder createTestOrder(String orderNumber, PurchaseOrderStatus status) {
        return PurchaseOrder.builder()
            .id(1)
            .purchaseOrderNumber(orderNumber)
            .orderDate(LocalDate.of(2025, 1, 1))
            .supplierCode("SUP-001")
            .status(status)
            .build();
    }

    private Supplier createTestSupplier() {
        return Supplier.builder()
            .supplierCode("SUP-001")
            .supplierName("テスト仕入先")
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .build();
    }

    private Item createTestItem() {
        return Item.builder()
            .id(1)
            .itemCode("MAT-001")
            .itemName("テスト材料")
            .itemCategory(ItemCategory.MATERIAL)
            .effectiveFrom(LocalDate.of(2025, 1, 1))
            .leadTime(10)
            .safetyStock(BigDecimal.TEN)
            .build();
    }
}
