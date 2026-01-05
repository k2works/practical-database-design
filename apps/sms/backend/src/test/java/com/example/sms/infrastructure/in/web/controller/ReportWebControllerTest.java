package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.dto.InventoryReportData;
import com.example.sms.application.service.ReportService;
import com.example.sms.domain.exception.InvoiceNotFoundException;
import com.example.sms.domain.exception.PurchaseOrderNotFoundException;
import com.example.sms.domain.exception.QuotationNotFoundException;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceDetail;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderDetail;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
import com.example.sms.infrastructure.in.web.service.PdfGeneratorService;
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
 * 帳票出力コントローラーテスト.
 */
@WebMvcTest(ReportWebController.class)
@DisplayName("帳票出力コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ReportWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private PdfGeneratorService pdfGeneratorService;

    @Nested
    @DisplayName("GET /reports/inventory/excel")
    class ExportInventoryExcel {

        @Test
        @DisplayName("在庫一覧を Excel 形式でエクスポートできる")
        void shouldExportInventoryAsExcel() throws Exception {
            List<InventoryReportData> inventoryList = List.of(
                InventoryReportData.builder()
                    .warehouseCode("WH001")
                    .warehouseName("本社倉庫")
                    .productCode("PRD001")
                    .productName("テスト商品")
                    .currentStock(new BigDecimal("100"))
                    .allocatedStock(new BigDecimal("20"))
                    .availableStock(new BigDecimal("80"))
                    .locationCode("A-01-01")
                    .build()
            );
            Mockito.when(reportService.getInventoryReport()).thenReturn(inventoryList);

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/inventory/excel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                    .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(MockMvcResultMatchers.header()
                    .string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment; filename=inventory_")));
        }

        @Test
        @DisplayName("在庫データが空でも Excel をエクスポートできる")
        void shouldExportEmptyInventoryAsExcel() throws Exception {
            Mockito.when(reportService.getInventoryReport()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/inventory/excel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                    .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        }
    }

    @Nested
    @DisplayName("GET /reports/invoice/{invoiceNumber}/pdf")
    class ExportInvoicePdf {

        @Test
        @DisplayName("請求書を PDF 形式でエクスポートできる")
        void shouldExportInvoiceAsPdf() throws Exception {
            Invoice invoice = createTestInvoice("INV-20240101-001");
            Mockito.when(reportService.getInvoiceForReport("INV-20240101-001")).thenReturn(invoice);
            Mockito.when(reportService.getCustomerName("CUST001")).thenReturn("テスト顧客株式会社");
            Mockito.when(pdfGeneratorService.generatePdf(
                ArgumentMatchers.eq("reports/invoice-pdf"),
                ArgumentMatchers.anyMap()
            )).thenReturn("PDF content".getBytes());

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/invoice/INV-20240101-001/pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/pdf"))
                .andExpect(MockMvcResultMatchers.header()
                    .string("Content-Disposition", "attachment; filename=invoice_INV-20240101-001.pdf"));
        }

        @Test
        @DisplayName("存在しない請求書の場合は 404 エラー")
        void shouldReturn404WhenInvoiceNotFound() throws Exception {
            Mockito.when(reportService.getInvoiceForReport("NOT-EXISTS"))
                .thenThrow(new InvoiceNotFoundException("NOT-EXISTS"));

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/invoice/NOT-EXISTS/pdf"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /reports/purchase-order/{purchaseOrderNumber}/pdf")
    class ExportPurchaseOrderPdf {

        @Test
        @DisplayName("発注書を PDF 形式でエクスポートできる")
        void shouldExportPurchaseOrderAsPdf() throws Exception {
            PurchaseOrder purchaseOrder = createTestPurchaseOrder("PO-20240101-001");
            Mockito.when(reportService.getPurchaseOrderForReport("PO-20240101-001")).thenReturn(purchaseOrder);
            Mockito.when(reportService.getSupplierName("SUP001")).thenReturn("テスト仕入先株式会社");
            Mockito.when(pdfGeneratorService.generatePdf(
                ArgumentMatchers.eq("reports/purchase-order-pdf"),
                ArgumentMatchers.anyMap()
            )).thenReturn("PDF content".getBytes());

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/purchase-order/PO-20240101-001/pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/pdf"))
                .andExpect(MockMvcResultMatchers.header()
                    .string("Content-Disposition", "attachment; filename=purchase_order_PO-20240101-001.pdf"));
        }

        @Test
        @DisplayName("存在しない発注書の場合は 404 エラー")
        void shouldReturn404WhenPurchaseOrderNotFound() throws Exception {
            Mockito.when(reportService.getPurchaseOrderForReport("NOT-EXISTS"))
                .thenThrow(new PurchaseOrderNotFoundException("NOT-EXISTS"));

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/purchase-order/NOT-EXISTS/pdf"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /reports/quotation/{quotationNumber}/pdf")
    class ExportQuotationPdf {

        @Test
        @DisplayName("見積書を PDF 形式でエクスポートできる")
        void shouldExportQuotationAsPdf() throws Exception {
            Quotation quotation = createTestQuotation("QT-20240101-001");
            Mockito.when(reportService.getQuotationForReport("QT-20240101-001")).thenReturn(quotation);
            Mockito.when(reportService.getCustomerName("CUST001")).thenReturn("テスト顧客株式会社");
            Mockito.when(pdfGeneratorService.generatePdf(
                ArgumentMatchers.eq("reports/quotation-pdf"),
                ArgumentMatchers.anyMap()
            )).thenReturn("PDF content".getBytes());

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/quotation/QT-20240101-001/pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/pdf"))
                .andExpect(MockMvcResultMatchers.header()
                    .string("Content-Disposition", "attachment; filename=quotation_QT-20240101-001.pdf"));
        }

        @Test
        @DisplayName("存在しない見積書の場合は 404 エラー")
        void shouldReturn404WhenQuotationNotFound() throws Exception {
            Mockito.when(reportService.getQuotationForReport("NOT-EXISTS"))
                .thenThrow(new QuotationNotFoundException("NOT-EXISTS"));

            mockMvc.perform(MockMvcRequestBuilders.get("/reports/quotation/NOT-EXISTS/pdf"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @SuppressWarnings("PMD.BigIntegerInstantiation")
    private PurchaseOrder createTestPurchaseOrder(String purchaseOrderNumber) {
        PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
            .id(1)
            .purchaseOrderId(1)
            .lineNumber(1)
            .productCode("PRD001")
            .orderQuantity(new BigDecimal("10"))
            .unitPrice(new BigDecimal("1000"))
            .orderAmount(new BigDecimal("10000"))
            .expectedDeliveryDate(LocalDate.of(2024, 1, 15))
            .receivedQuantity(BigDecimal.ZERO)
            .remainingQuantity(new BigDecimal("10"))
            .build();

        return PurchaseOrder.builder()
            .id(1)
            .purchaseOrderNumber(purchaseOrderNumber)
            .supplierCode("SUP001")
            .orderDate(LocalDate.of(2024, 1, 1))
            .desiredDeliveryDate(LocalDate.of(2024, 1, 15))
            .status(PurchaseOrderStatus.CONFIRMED)
            .totalAmount(new BigDecimal("10000"))
            .taxAmount(new BigDecimal("1000"))
            .details(List.of(detail))
            .build();
    }

    private Invoice createTestInvoice(String invoiceNumber) {
        InvoiceDetail detail = InvoiceDetail.builder()
            .id(1)
            .invoiceId(1)
            .lineNumber(1)
            .salesId(1)
            .salesNumber("SLS-001")
            .salesDate(LocalDate.of(2024, 1, 15))
            .salesAmount(new BigDecimal("10000"))
            .taxAmount(new BigDecimal("1000"))
            .totalAmount(new BigDecimal("11000"))
            .build();

        return Invoice.builder()
            .id(1)
            .invoiceNumber(invoiceNumber)
            .invoiceDate(LocalDate.of(2024, 1, 31))
            .customerCode("CUST001")
            .previousBalance(BigDecimal.ZERO)
            .receiptAmount(BigDecimal.ZERO)
            .carriedBalance(BigDecimal.ZERO)
            .currentSalesAmount(new BigDecimal("10000"))
            .currentTaxAmount(new BigDecimal("1000"))
            .currentInvoiceAmount(new BigDecimal("11000"))
            .invoiceBalance(new BigDecimal("11000"))
            .dueDate(LocalDate.of(2024, 2, 28))
            .status(InvoiceStatus.ISSUED)
            .details(List.of(detail))
            .build();
    }

    @SuppressWarnings("PMD.BigIntegerInstantiation")
    private Quotation createTestQuotation(String quotationNumber) {
        QuotationDetail detail = QuotationDetail.builder()
            .id(1)
            .quotationId(1)
            .lineNumber(1)
            .productCode("PRD001")
            .productName("テスト商品")
            .quantity(new BigDecimal("5"))
            .unit("個")
            .unitPrice(new BigDecimal("2000"))
            .amount(new BigDecimal("10000"))
            .build();

        return Quotation.builder()
            .id(1)
            .quotationNumber(quotationNumber)
            .quotationDate(LocalDate.of(2024, 1, 1))
            .validUntil(LocalDate.of(2024, 2, 1))
            .customerCode("CUST001")
            .subject("テスト見積")
            .subtotal(new BigDecimal("10000"))
            .taxAmount(new BigDecimal("1000"))
            .totalAmount(new BigDecimal("11000"))
            .status(QuotationStatus.NEGOTIATING)
            .details(List.of(detail))
            .build();
    }
}
