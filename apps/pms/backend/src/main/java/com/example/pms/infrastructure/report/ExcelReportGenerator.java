package com.example.pms.infrastructure.report;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Excel 帳票生成.
 */
@Component
public class ExcelReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String[] HEADERS = {"発注番号", "発注日", "仕入先コード", "仕入先名", "ステータス", "備考"};

    /**
     * 発注一覧 Excel を生成する.
     *
     * @param orders 発注リスト
     * @return Excel バイト配列
     */
    public byte[] generatePurchaseOrderList(List<PurchaseOrder> orders) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("発注一覧");

            createTitleRow(workbook, sheet);
            createDateRow(sheet);
            createHeaderRow(workbook, sheet);
            createDataRows(workbook, sheet, orders);
            createTotalRow(sheet, orders.size());
            autoSizeColumns(sheet);

            return toByteArray(workbook);

        } catch (IOException e) {
            throw new ReportGenerationException("Excel 生成に失敗しました", e);
        }
    }

    private void createTitleRow(Workbook workbook, Sheet sheet) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("発注一覧");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
    }

    private void createDateRow(Sheet sheet) {
        Row dateRow = sheet.createRow(1);
        dateRow.createCell(0).setCellValue("出力日: " + LocalDate.now().format(DATE_FORMATTER));
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(3);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void createDataRows(Workbook workbook, Sheet sheet, List<PurchaseOrder> orders) {
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        int rowNum = 4;
        for (PurchaseOrder order : orders) {
            Row row = sheet.createRow(rowNum++);
            createDataRow(row, order, dataStyle, dateStyle);
        }
    }

    private void createDataRow(Row row, PurchaseOrder order, CellStyle dataStyle, CellStyle dateStyle) {
        createCell(row, 0, order.getPurchaseOrderNumber(), dataStyle);
        createCell(row, 1, formatDate(order.getOrderDate()), dateStyle);
        createCell(row, 2, order.getSupplierCode(), dataStyle);
        createCell(row, 3, getSupplierName(order), dataStyle);
        createCell(row, 4, getStatusName(order), dataStyle);
        createCell(row, 5, order.getRemarks(), dataStyle);
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createTotalRow(Sheet sheet, int count) {
        Row totalRow = sheet.createRow(count + 5);
        totalRow.createCell(0).setCellValue("合計: " + count + " 件");
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private byte[] toByteArray(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    private String getSupplierName(PurchaseOrder order) {
        return order.getSupplier() != null ? order.getSupplier().getSupplierName() : "";
    }

    private String getStatusName(PurchaseOrder order) {
        return order.getStatus() != null ? order.getStatus().getDisplayName() : "";
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
