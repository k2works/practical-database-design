package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.ReportUseCase;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.report.BalanceSheet;
import com.example.fas.domain.model.report.DailyReport;
import com.example.fas.domain.model.report.GeneralLedger;
import com.example.fas.domain.model.report.GeneralLedgerEntry;
import com.example.fas.domain.model.report.IncomeStatement;
import com.example.fas.domain.model.report.TrialBalance;
import com.example.fas.infrastructure.in.web.service.PdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 帳票出力コントローラー.
 */
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods"})
public class ReportWebController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ReportUseCase reportUseCase;
    private final PdfGeneratorService pdfGeneratorService;

    // ========================================
    // 日計表
    // ========================================

    /**
     * 日計表一覧（HTML）.
     */
    @GetMapping("/daily")
    public String dailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyReport report = reportUseCase.getDailyReport(targetDate);

        model.addAttribute("report", report);
        model.addAttribute("date", targetDate);
        return "reports/daily";
    }

    /**
     * 日計表Excel出力.
     */
    @GetMapping("/daily/excel")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void dailyReportExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws IOException {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyReport report = reportUseCase.getDailyReport(targetDate);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=daily_report_" + targetDate + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("日計表");
            CellStyle headerStyle = createHeaderStyle(workbook);

            // ヘッダー行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"起票日", "勘定科目コード", "勘定科目名", "BSPL区分", "貸借区分",
                                "借方合計", "貸方合計", "残高"};
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // データ行
            int rowNum = 1;
            for (var line : report.getLines()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(line.getPostingDate().toString());
                row.createCell(1).setCellValue(line.getAccountCode());
                row.createCell(2).setCellValue(line.getAccountName());
                row.createCell(3).setCellValue(line.getBsplType());
                row.createCell(4).setCellValue(line.getDebitCreditType());
                row.createCell(5).setCellValue(line.getDebitTotal().doubleValue());
                row.createCell(6).setCellValue(line.getCreditTotal().doubleValue());
                row.createCell(7).setCellValue(line.getBalance().doubleValue());
            }

            // 列幅自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 日計表PDF出力.
     */
    @GetMapping("/daily/pdf")
    public void dailyReportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws IOException {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailyReport report = reportUseCase.getDailyReport(targetDate);

        Map<String, Object> variables = Map.of(
            "report", report,
            "date", targetDate
        );

        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/daily-pdf", variables);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=daily_report_" + targetDate + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    // ========================================
    // 総勘定元帳
    // ========================================

    /**
     * 総勘定元帳一覧（HTML、ページネーション対応）.
     */
    @GetMapping("/general-ledger")
    public String generalLedger(
            @RequestParam(required = false) String accountCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        String targetAccountCode = accountCode != null ? accountCode : "11110";
        LocalDate targetFromDate = fromDate != null ? fromDate : LocalDate.now().withDayOfMonth(1);
        LocalDate targetToDate = toDate != null ? toDate : LocalDate.now();

        GeneralLedger meta = reportUseCase.getGeneralLedgerMeta(
                targetAccountCode, targetFromDate, targetToDate);
        PageResult<GeneralLedgerEntry> entries = reportUseCase.getGeneralLedger(
                targetAccountCode, targetFromDate, targetToDate, page, size);

        model.addAttribute("meta", meta);
        model.addAttribute("entries", entries.getContent());
        model.addAttribute("page", entries);
        model.addAttribute("accountCode", targetAccountCode);
        model.addAttribute("fromDate", targetFromDate);
        model.addAttribute("toDate", targetToDate);
        model.addAttribute("currentSize", size);

        return "reports/general-ledger";
    }

    /**
     * 総勘定元帳Excel出力.
     */
    @GetMapping("/general-ledger/excel")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void generalLedgerExcel(
            @RequestParam String accountCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletResponse response) throws IOException {

        GeneralLedger meta = reportUseCase.getGeneralLedgerMeta(accountCode, fromDate, toDate);
        PageResult<GeneralLedgerEntry> entries = reportUseCase.getGeneralLedger(
                accountCode, fromDate, toDate, 0, 10_000);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=general_ledger_" + accountCode + "_" + fromDate + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("総勘定元帳");
            CellStyle headerStyle = createHeaderStyle(workbook);

            // タイトル行
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("勘定科目: " + meta.getAccountName()
                    + " (" + accountCode + ")");

            // ヘッダー行
            Row headerRow = sheet.createRow(2);
            String[] headers = {"起票日", "伝票番号", "摘要", "相手勘定", "借方", "貸方", "残高"};
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // データ行
            int rowNum = 3;
            for (var entry : entries.getContent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getPostingDate().toString());
                row.createCell(1).setCellValue(entry.getJournalNumber());
                row.createCell(2).setCellValue(entry.getDescription() != null ? entry.getDescription() : "");
                row.createCell(3).setCellValue(entry.getCounterAccountName());
                row.createCell(4).setCellValue(entry.getDebitAmount() != null
                        ? entry.getDebitAmount().doubleValue() : 0);
                row.createCell(5).setCellValue(entry.getCreditAmount() != null
                        ? entry.getCreditAmount().doubleValue() : 0);
                row.createCell(6).setCellValue(entry.getBalance() != null
                        ? entry.getBalance().doubleValue() : 0);
            }

            // 列幅自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 総勘定元帳PDF出力.
     */
    @GetMapping("/general-ledger/pdf")
    public void generalLedgerPdf(
            @RequestParam String accountCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletResponse response) throws IOException {

        GeneralLedger meta = reportUseCase.getGeneralLedgerMeta(accountCode, fromDate, toDate);
        PageResult<GeneralLedgerEntry> entries = reportUseCase.getGeneralLedger(
                accountCode, fromDate, toDate, 0, 10_000);

        Map<String, Object> variables = Map.of(
            "meta", meta,
            "entries", entries.getContent(),
            "fromDate", fromDate,
            "toDate", toDate
        );

        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/general-ledger-pdf", variables);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=general_ledger_" + accountCode + "_" + fromDate + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    // ========================================
    // 貸借対照表
    // ========================================

    /**
     * 貸借対照表一覧（HTML）.
     */
    @GetMapping("/balance-sheet")
    public String balanceSheet(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            Model model) {

        LocalDate targetDate = asOfDate != null ? asOfDate : LocalDate.now();
        BalanceSheet report = reportUseCase.getBalanceSheet(targetDate);

        model.addAttribute("report", report);
        model.addAttribute("asOfDate", targetDate);
        return "reports/balance-sheet";
    }

    /**
     * 貸借対照表Excel出力.
     */
    @GetMapping("/balance-sheet/excel")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void balanceSheetExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            HttpServletResponse response) throws IOException {

        LocalDate targetDate = asOfDate != null ? asOfDate : LocalDate.now();
        BalanceSheet report = reportUseCase.getBalanceSheet(targetDate);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=balance_sheet_" + targetDate + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("貸借対照表");

            // タイトル
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("貸借対照表 " + targetDate);

            // 資産の部
            int rowNum = 2;
            Row assetHeader = sheet.createRow(rowNum++);
            assetHeader.createCell(0).setCellValue("【資産の部】");

            for (var line : report.getCurrentAssets()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(1).setCellValue(line.getAccountName());
                row.createCell(2).setCellValue(line.getAmount().doubleValue());
            }

            Row currentAssetTotal = sheet.createRow(rowNum++);
            currentAssetTotal.createCell(1).setCellValue("流動資産合計");
            currentAssetTotal.createCell(2).setCellValue(report.getTotalCurrentAssets().doubleValue());

            for (var line : report.getFixedAssets()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(1).setCellValue(line.getAccountName());
                row.createCell(2).setCellValue(line.getAmount().doubleValue());
            }

            Row fixedAssetTotal = sheet.createRow(rowNum++);
            fixedAssetTotal.createCell(1).setCellValue("固定資産合計");
            fixedAssetTotal.createCell(2).setCellValue(report.getTotalFixedAssets().doubleValue());

            Row assetTotal = sheet.createRow(rowNum);
            assetTotal.createCell(0).setCellValue("資産合計");
            assetTotal.createCell(2).setCellValue(report.getTotalAssets().doubleValue());

            // 列幅自動調整
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 貸借対照表PDF出力.
     */
    @GetMapping("/balance-sheet/pdf")
    public void balanceSheetPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            HttpServletResponse response) throws IOException {

        LocalDate targetDate = asOfDate != null ? asOfDate : LocalDate.now();
        BalanceSheet report = reportUseCase.getBalanceSheet(targetDate);

        Map<String, Object> variables = Map.of(
            "report", report,
            "asOfDate", targetDate
        );

        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/balance-sheet-pdf", variables);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=balance_sheet_" + targetDate + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    // ========================================
    // 損益計算書
    // ========================================

    /**
     * 損益計算書一覧（HTML）.
     */
    @GetMapping("/income-statement")
    public String incomeStatement(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth fromMonth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth toMonth,
            Model model) {

        YearMonth targetFromMonth = fromMonth != null ? fromMonth : YearMonth.now().withMonth(1);
        YearMonth targetToMonth = toMonth != null ? toMonth : YearMonth.now();
        IncomeStatement report = reportUseCase.getIncomeStatement(targetFromMonth, targetToMonth);

        model.addAttribute("report", report);
        model.addAttribute("fromMonth", targetFromMonth);
        model.addAttribute("toMonth", targetToMonth);
        return "reports/income-statement";
    }

    /**
     * 損益計算書Excel出力.
     */
    @GetMapping("/income-statement/excel")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void incomeStatementExcel(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth fromMonth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth toMonth,
            HttpServletResponse response) throws IOException {

        YearMonth targetFromMonth = fromMonth != null ? fromMonth : YearMonth.now().withMonth(1);
        YearMonth targetToMonth = toMonth != null ? toMonth : YearMonth.now();
        IncomeStatement report = reportUseCase.getIncomeStatement(targetFromMonth, targetToMonth);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=income_statement_" + targetFromMonth + "_" + targetToMonth + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("損益計算書");

            // タイトル
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("損益計算書 " + targetFromMonth + " 〜 " + targetToMonth);

            int rowNum = 2;

            // 売上高
            Row salesHeader = sheet.createRow(rowNum++);
            salesHeader.createCell(0).setCellValue("【売上高】");
            for (var line : report.getSalesRevenue()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(1).setCellValue(line.getAccountName());
                row.createCell(2).setCellValue(line.getAmount().doubleValue());
            }
            Row salesTotal = sheet.createRow(rowNum++);
            salesTotal.createCell(1).setCellValue("売上高合計");
            salesTotal.createCell(2).setCellValue(report.getTotalSalesRevenue().doubleValue());

            // 売上総利益
            Row grossProfit = sheet.createRow(rowNum++);
            grossProfit.createCell(0).setCellValue("売上総利益");
            grossProfit.createCell(2).setCellValue(report.getGrossProfit().doubleValue());

            // 営業利益
            Row operatingIncome = sheet.createRow(rowNum++);
            operatingIncome.createCell(0).setCellValue("営業利益");
            operatingIncome.createCell(2).setCellValue(report.getOperatingIncome().doubleValue());

            // 経常利益
            Row ordinaryIncome = sheet.createRow(rowNum++);
            ordinaryIncome.createCell(0).setCellValue("経常利益");
            ordinaryIncome.createCell(2).setCellValue(report.getOrdinaryIncome().doubleValue());

            // 当期純利益
            Row netIncome = sheet.createRow(rowNum);
            netIncome.createCell(0).setCellValue("当期純利益");
            netIncome.createCell(2).setCellValue(report.getNetIncome().doubleValue());

            // 列幅自動調整
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 損益計算書PDF出力.
     */
    @GetMapping("/income-statement/pdf")
    public void incomeStatementPdf(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth fromMonth,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth toMonth,
            HttpServletResponse response) throws IOException {

        YearMonth targetFromMonth = fromMonth != null ? fromMonth : YearMonth.now().withMonth(1);
        YearMonth targetToMonth = toMonth != null ? toMonth : YearMonth.now();
        IncomeStatement report = reportUseCase.getIncomeStatement(targetFromMonth, targetToMonth);

        Map<String, Object> variables = Map.of(
            "report", report,
            "fromMonth", targetFromMonth,
            "toMonth", targetToMonth
        );

        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/income-statement-pdf", variables);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=income_statement_" + targetFromMonth + "_" + targetToMonth + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    // ========================================
    // 合計残高試算表
    // ========================================

    /**
     * 合計残高試算表一覧（HTML）.
     */
    @GetMapping("/trial-balance")
    public String trialBalance(
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) Integer month,
            Model model) {

        int targetFiscalYear = fiscalYear != null ? fiscalYear : LocalDate.now().getYear();
        int targetMonth = month != null ? month : LocalDate.now().getMonthValue();
        TrialBalance report = reportUseCase.getTrialBalance(targetFiscalYear, targetMonth);

        model.addAttribute("report", report);
        model.addAttribute("fiscalYear", targetFiscalYear);
        model.addAttribute("month", targetMonth);
        return "reports/trial-balance";
    }

    /**
     * 合計残高試算表Excel出力.
     */
    @GetMapping("/trial-balance/excel")
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public void trialBalanceExcel(
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) Integer month,
            HttpServletResponse response) throws IOException {

        int targetFiscalYear = fiscalYear != null ? fiscalYear : LocalDate.now().getYear();
        int targetMonth = month != null ? month : LocalDate.now().getMonthValue();
        TrialBalance report = reportUseCase.getTrialBalance(targetFiscalYear, targetMonth);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=trial_balance_" + targetFiscalYear + "_" + targetMonth + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("合計残高試算表");
            CellStyle headerStyle = createHeaderStyle(workbook);

            // タイトル
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue(
                    "合計残高試算表 " + targetFiscalYear + "年度 " + targetMonth + "月");

            // ヘッダー行
            Row headerRow = sheet.createRow(2);
            String[] headers = {"勘定科目コード", "勘定科目名", "BSPL区分", "貸借区分",
                                "月初残高", "借方合計", "貸方合計", "月末残高"};
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // データ行
            int rowNum = 3;
            for (var line : report.getLines()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(line.getAccountCode());
                row.createCell(1).setCellValue(line.getAccountName());
                row.createCell(2).setCellValue(line.getBsplType());
                row.createCell(3).setCellValue(line.getDebitCreditType());
                row.createCell(4).setCellValue(
                        line.getOpeningBalance() != null ? line.getOpeningBalance().doubleValue() : 0);
                row.createCell(5).setCellValue(
                        line.getDebitTotal() != null ? line.getDebitTotal().doubleValue() : 0);
                row.createCell(6).setCellValue(
                        line.getCreditTotal() != null ? line.getCreditTotal().doubleValue() : 0);
                row.createCell(7).setCellValue(
                        line.getClosingBalance() != null ? line.getClosingBalance().doubleValue() : 0);
            }

            // 合計行
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(1).setCellValue("合計");
            totalRow.createCell(4).setCellValue(report.getTotalOpeningDebit().doubleValue());
            totalRow.createCell(5).setCellValue(report.getTotalDebit().doubleValue());
            totalRow.createCell(6).setCellValue(report.getTotalCredit().doubleValue());
            totalRow.createCell(7).setCellValue(report.getTotalClosingDebit().doubleValue());

            // 列幅自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 合計残高試算表PDF出力.
     */
    @GetMapping("/trial-balance/pdf")
    public void trialBalancePdf(
            @RequestParam(required = false) Integer fiscalYear,
            @RequestParam(required = false) Integer month,
            HttpServletResponse response) throws IOException {

        int targetFiscalYear = fiscalYear != null ? fiscalYear : LocalDate.now().getYear();
        int targetMonth = month != null ? month : LocalDate.now().getMonthValue();
        TrialBalance report = reportUseCase.getTrialBalance(targetFiscalYear, targetMonth);

        Map<String, Object> variables = Map.of(
            "report", report,
            "fiscalYear", targetFiscalYear,
            "month", targetMonth
        );

        byte[] pdfBytes = pdfGeneratorService.generatePdf("reports/trial-balance-pdf", variables);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=trial_balance_" + targetFiscalYear + "_" + targetMonth + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    // ========================================
    // 共通メソッド
    // ========================================

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
}
