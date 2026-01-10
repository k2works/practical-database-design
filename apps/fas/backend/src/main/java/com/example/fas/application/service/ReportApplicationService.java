package com.example.fas.application.service;

import com.example.fas.application.port.in.ReportUseCase;
import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.DailyAccountBalanceRepository;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.application.port.out.MonthlyAccountBalanceRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.balance.DailyReportLine;
import com.example.fas.domain.model.balance.TrialBalanceLine;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.report.BalanceSheet;
import com.example.fas.domain.model.report.BalanceSheetLine;
import com.example.fas.domain.model.report.DailyReport;
import com.example.fas.domain.model.report.GeneralLedger;
import com.example.fas.domain.model.report.GeneralLedgerEntry;
import com.example.fas.domain.model.report.IncomeStatement;
import com.example.fas.domain.model.report.IncomeStatementLine;
import com.example.fas.domain.model.report.TrialBalance;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帳票出力アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class ReportApplicationService implements ReportUseCase {

    private final DailyAccountBalanceRepository dailyBalanceRepository;
    private final MonthlyAccountBalanceRepository monthlyBalanceRepository;
    private final JournalRepository journalRepository;
    private final AccountRepository accountRepository;

    @Override
    public DailyReport getDailyReport(LocalDate date) {
        List<DailyReportLine> lines = dailyBalanceRepository.getDailyReport(date);
        return DailyReport.of(date, lines);
    }

    @Override
    public PageResult<GeneralLedgerEntry> getGeneralLedger(
            String accountCode,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {
        return journalRepository.findGeneralLedgerEntries(accountCode, fromDate, toDate, page, size);
    }

    @Override
    public GeneralLedger getGeneralLedgerMeta(
            String accountCode,
            LocalDate fromDate,
            LocalDate toDate) {

        Account account = accountRepository.findByCode(accountCode)
                .orElseThrow(() -> new IllegalArgumentException("勘定科目が見つかりません: " + accountCode));

        BigDecimal openingBalance = journalRepository.getOpeningBalance(
                accountCode, fromDate.minusDays(1));

        return GeneralLedger.builder()
                .accountCode(accountCode)
                .accountName(account.getAccountName())
                .bsplType(account.getBsplType().name())
                .debitCreditType(account.getDebitCreditType().getDisplayName())
                .fromDate(fromDate)
                .toDate(toDate)
                .openingBalance(openingBalance != null ? openingBalance : BigDecimal.ZERO)
                .entries(List.of())
                .build();
    }

    @Override
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public BalanceSheet getBalanceSheet(LocalDate asOfDate) {
        // 決算期と月度を計算（仮で年度 = 年、月度 = 月とする）
        int fiscalYear = asOfDate.getYear();
        int month = asOfDate.getMonthValue();

        // BS 区分の試算表データを取得
        List<TrialBalanceLine> bsLines = monthlyBalanceRepository.getTrialBalanceByBSPL(
                fiscalYear, month, "BS");

        // 資産と負債・純資産を分類
        List<BalanceSheetLine> currentAssets = new ArrayList<>();
        List<BalanceSheetLine> fixedAssets = new ArrayList<>();
        List<BalanceSheetLine> currentLiabilities = new ArrayList<>();
        List<BalanceSheetLine> fixedLiabilities = new ArrayList<>();
        List<BalanceSheetLine> equity = new ArrayList<>();

        BigDecimal totalCurrentAssets = BigDecimal.ZERO;
        BigDecimal totalFixedAssets = BigDecimal.ZERO;
        BigDecimal totalCurrentLiabilities = BigDecimal.ZERO;
        BigDecimal totalFixedLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        for (TrialBalanceLine line : bsLines) {
            BalanceSheetLine bsLine = BalanceSheetLine.builder()
                    .accountCode(line.getAccountCode())
                    .accountName(line.getAccountName())
                    .amount(line.getClosingBalance())
                    .indentLevel(1)
                    .isSummary(false)
                    .build();

            // 勘定科目コードの最初の1桁で分類
            String code = line.getAccountCode();
            if (code.startsWith("1")) {
                // 資産
                if (code.startsWith("11") || code.startsWith("12") || code.startsWith("13")) {
                    currentAssets.add(bsLine);
                    totalCurrentAssets = totalCurrentAssets.add(line.getClosingBalance());
                } else {
                    fixedAssets.add(bsLine);
                    totalFixedAssets = totalFixedAssets.add(line.getClosingBalance());
                }
            } else if (code.startsWith("2")) {
                // 負債
                if (code.startsWith("21") || code.startsWith("22")) {
                    currentLiabilities.add(bsLine);
                    totalCurrentLiabilities = totalCurrentLiabilities.add(line.getClosingBalance());
                } else {
                    fixedLiabilities.add(bsLine);
                    totalFixedLiabilities = totalFixedLiabilities.add(line.getClosingBalance());
                }
            } else if (code.startsWith("3")) {
                // 純資産
                equity.add(bsLine);
                totalEquity = totalEquity.add(line.getClosingBalance());
            }
        }

        BigDecimal totalAssets = totalCurrentAssets.add(totalFixedAssets);
        BigDecimal totalLiabilities = totalCurrentLiabilities.add(totalFixedLiabilities);
        BigDecimal totalLiabilitiesAndEquity = totalLiabilities.add(totalEquity);

        return BalanceSheet.builder()
                .asOfDate(asOfDate)
                .fiscalYear(fiscalYear)
                .currentAssets(currentAssets)
                .fixedAssets(fixedAssets)
                .totalCurrentAssets(totalCurrentAssets)
                .totalFixedAssets(totalFixedAssets)
                .totalAssets(totalAssets)
                .currentLiabilities(currentLiabilities)
                .fixedLiabilities(fixedLiabilities)
                .totalCurrentLiabilities(totalCurrentLiabilities)
                .totalFixedLiabilities(totalFixedLiabilities)
                .totalLiabilities(totalLiabilities)
                .equity(equity)
                .totalEquity(totalEquity)
                .totalLiabilitiesAndEquity(totalLiabilitiesAndEquity)
                .build();
    }

    @Override
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public IncomeStatement getIncomeStatement(YearMonth fromMonth, YearMonth toMonth) {
        int fiscalYear = fromMonth.getYear();
        int month = toMonth.getMonthValue();

        // PL 区分の試算表データを取得
        List<TrialBalanceLine> plLines = monthlyBalanceRepository.getTrialBalanceByBSPL(
                fiscalYear, month, "PL");

        // 収益・費用を分類
        List<IncomeStatementLine> salesRevenue = new ArrayList<>();
        List<IncomeStatementLine> costOfSales = new ArrayList<>();
        List<IncomeStatementLine> sellingGeneralExpenses = new ArrayList<>();
        List<IncomeStatementLine> nonOperatingIncome = new ArrayList<>();
        List<IncomeStatementLine> nonOperatingExpenses = new ArrayList<>();
        List<IncomeStatementLine> extraordinaryIncome = new ArrayList<>();
        List<IncomeStatementLine> extraordinaryLoss = new ArrayList<>();

        BigDecimal totalSalesRevenue = BigDecimal.ZERO;
        BigDecimal totalCostOfSales = BigDecimal.ZERO;
        BigDecimal totalSellingGeneralExpenses = BigDecimal.ZERO;
        BigDecimal totalNonOperatingIncome = BigDecimal.ZERO;
        BigDecimal totalNonOperatingExpenses = BigDecimal.ZERO;
        BigDecimal totalExtraordinaryIncome = BigDecimal.ZERO;
        BigDecimal totalExtraordinaryLoss = BigDecimal.ZERO;

        for (TrialBalanceLine line : plLines) {
            IncomeStatementLine plLine = IncomeStatementLine.builder()
                    .accountCode(line.getAccountCode())
                    .accountName(line.getAccountName())
                    .amount(line.getClosingBalance())
                    .indentLevel(1)
                    .isSummary(false)
                    .build();

            // 勘定科目コードの最初の1桁で分類
            String code = line.getAccountCode();
            if (code.startsWith("4")) {
                // 売上高
                salesRevenue.add(plLine);
                totalSalesRevenue = totalSalesRevenue.add(line.getClosingBalance());
            } else if (code.startsWith("5")) {
                // 売上原価
                costOfSales.add(plLine);
                totalCostOfSales = totalCostOfSales.add(line.getClosingBalance());
            } else if (code.startsWith("6")) {
                // 販管費
                sellingGeneralExpenses.add(plLine);
                totalSellingGeneralExpenses = totalSellingGeneralExpenses.add(line.getClosingBalance());
            } else if (code.startsWith("7")) {
                // 営業外収益
                nonOperatingIncome.add(plLine);
                totalNonOperatingIncome = totalNonOperatingIncome.add(line.getClosingBalance());
            } else if (code.startsWith("8")) {
                // 営業外費用・特別損益
                if (code.startsWith("81")) {
                    nonOperatingExpenses.add(plLine);
                    totalNonOperatingExpenses = totalNonOperatingExpenses.add(line.getClosingBalance());
                } else if (code.startsWith("82")) {
                    extraordinaryIncome.add(plLine);
                    totalExtraordinaryIncome = totalExtraordinaryIncome.add(line.getClosingBalance());
                } else {
                    extraordinaryLoss.add(plLine);
                    totalExtraordinaryLoss = totalExtraordinaryLoss.add(line.getClosingBalance());
                }
            }
        }

        // 利益計算
        BigDecimal grossProfit = totalSalesRevenue.subtract(totalCostOfSales);
        BigDecimal operatingIncome = grossProfit.subtract(totalSellingGeneralExpenses);
        BigDecimal ordinaryIncome = operatingIncome
                .add(totalNonOperatingIncome)
                .subtract(totalNonOperatingExpenses);
        BigDecimal incomeBeforeTaxes = ordinaryIncome
                .add(totalExtraordinaryIncome)
                .subtract(totalExtraordinaryLoss);

        // 法人税等は仮で 30%
        BigDecimal incomeTaxes = incomeBeforeTaxes.compareTo(BigDecimal.ZERO) > 0
                ? incomeBeforeTaxes.multiply(new BigDecimal("0.30"))
                : BigDecimal.ZERO;
        BigDecimal netIncome = incomeBeforeTaxes.subtract(incomeTaxes);

        return IncomeStatement.builder()
                .fiscalYear(fiscalYear)
                .fromMonth(fromMonth)
                .toMonth(toMonth)
                .salesRevenue(salesRevenue)
                .totalSalesRevenue(totalSalesRevenue)
                .costOfSales(costOfSales)
                .totalCostOfSales(totalCostOfSales)
                .grossProfit(grossProfit)
                .sellingGeneralExpenses(sellingGeneralExpenses)
                .totalSellingGeneralExpenses(totalSellingGeneralExpenses)
                .operatingIncome(operatingIncome)
                .nonOperatingIncome(nonOperatingIncome)
                .totalNonOperatingIncome(totalNonOperatingIncome)
                .nonOperatingExpenses(nonOperatingExpenses)
                .totalNonOperatingExpenses(totalNonOperatingExpenses)
                .ordinaryIncome(ordinaryIncome)
                .extraordinaryIncome(extraordinaryIncome)
                .totalExtraordinaryIncome(totalExtraordinaryIncome)
                .extraordinaryLoss(extraordinaryLoss)
                .totalExtraordinaryLoss(totalExtraordinaryLoss)
                .incomeBeforeTaxes(incomeBeforeTaxes)
                .incomeTaxes(incomeTaxes)
                .netIncome(netIncome)
                .build();
    }

    @Override
    public TrialBalance getTrialBalance(Integer fiscalYear, Integer month) {
        List<TrialBalanceLine> lines = monthlyBalanceRepository.getTrialBalance(fiscalYear, month);
        return TrialBalance.of(fiscalYear, month, lines);
    }
}
