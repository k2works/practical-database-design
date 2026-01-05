package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.infrastructure.in.web.dto.InventoryReportData;
import com.example.sms.application.service.ReportService;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.infrastructure.in.web.service.PdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 帳票出力コントローラー.
 */
@Controller
@RequestMapping("/reports")
public class ReportWebController {

    private final ReportService reportService;
    private final PdfGeneratorService pdfGeneratorService;

    public ReportWebController(ReportService reportService, PdfGeneratorService pdfGeneratorService) {
        this.reportService = reportService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    /**
     * 在庫一覧を Excel 形式でエクスポート.
     */
    @GetMapping("/inventory/excel")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void exportInventoryExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=inventory_" + LocalDate.now() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("在庫一覧");

            // ヘッダースタイル
            CellStyle headerStyle = createHeaderStyle(workbook);

            // ヘッダー行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"倉庫コード", "倉庫名", "商品コード", "商品名", "現在庫数", "引当数", "有効在庫数", "ロケーション"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // データ行
            List<InventoryReportData> inventoryList = reportService.getInventoryReport();
            int rowNum = 1;
            for (InventoryReportData data : inventoryList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getWarehouseCode());
                row.createCell(1).setCellValue(data.getWarehouseName());
                row.createCell(2).setCellValue(data.getProductCode());
                row.createCell(3).setCellValue(data.getProductName());
                row.createCell(4).setCellValue(formatNumber(data.getCurrentStock()));
                row.createCell(5).setCellValue(formatNumber(data.getAllocatedStock()));
                row.createCell(6).setCellValue(formatNumber(data.getAvailableStock()));
                row.createCell(7).setCellValue(data.getLocationCode() != null ? data.getLocationCode() : "");
            }

            // 列幅自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 請求書を PDF 形式でエクスポート.
     */
    @GetMapping("/invoice/{invoiceNumber}/pdf")
    public void exportInvoicePdf(@PathVariable String invoiceNumber, HttpServletResponse response)
            throws IOException {

        Invoice invoice = reportService.getInvoiceForReport(invoiceNumber);
        String customerName = reportService.getCustomerName(invoice.getCustomerCode());

        // テンプレート変数を設定
        Map<String, Object> variables = Map.of(
            "invoice", invoice,
            "customerName", customerName
        );

        // PDFを生成
        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/invoice-pdf", variables);

        // レスポンスを設定
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=invoice_" + invoiceNumber + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    /**
     * 発注書を PDF 形式でエクスポート.
     */
    @GetMapping("/purchase-order/{purchaseOrderNumber}/pdf")
    public void exportPurchaseOrderPdf(@PathVariable String purchaseOrderNumber, HttpServletResponse response)
            throws IOException {

        PurchaseOrder purchaseOrder = reportService.getPurchaseOrderForReport(purchaseOrderNumber);
        String supplierName = reportService.getSupplierName(purchaseOrder.getSupplierCode());

        // テンプレート変数を設定
        Map<String, Object> variables = Map.of(
            "purchaseOrder", purchaseOrder,
            "supplierName", supplierName
        );

        // PDFを生成
        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/purchase-order-pdf", variables);

        // レスポンスを設定
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=purchase_order_" + purchaseOrderNumber + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    /**
     * 見積書を PDF 形式でエクスポート.
     */
    @GetMapping("/quotation/{quotationNumber}/pdf")
    public void exportQuotationPdf(@PathVariable String quotationNumber, HttpServletResponse response)
            throws IOException {

        Quotation quotation = reportService.getQuotationForReport(quotationNumber);
        String customerName = reportService.getCustomerName(quotation.getCustomerCode());

        // テンプレート変数を設定
        Map<String, Object> variables = Map.of(
            "quotation", quotation,
            "customerName", customerName
        );

        // PDFを生成
        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/quotation-pdf", variables);

        // レスポンスを設定
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=quotation_" + quotationNumber + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private String formatNumber(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return new java.text.DecimalFormat("#,##0").format(value);
    }
}
