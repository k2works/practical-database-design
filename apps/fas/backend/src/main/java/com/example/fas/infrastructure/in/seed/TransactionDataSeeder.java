package com.example.fas.infrastructure.in.seed;

import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.journal.JournalVoucherType;
import com.example.fas.domain.model.journal.TaxType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * トランザクションデータ Seeder.
 * D社事例（化粧品製造販売会社）に基づくサンプル仕訳データを投入する。
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
public class TransactionDataSeeder {

    private final JournalRepository journalRepository;

    /**
     * すべてのトランザクションデータを投入.
     */
    public void seedAll() {
        log.info("トランザクションデータを投入中...");
        seedSampleJournals();
        log.info("トランザクションデータ投入完了");
    }

    /**
     * すべてのトランザクションデータを削除.
     */
    public void cleanAll() {
        log.info("トランザクションデータを削除中...");
        journalRepository.deleteAll();
        log.info("トランザクションデータ削除完了");
    }

    /**
     * サンプル仕訳データを投入.
     * D社事例 2025年4月度のサンプル仕訳。
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.NPathComplexity"})
    private void seedSampleJournals() {
        log.info("サンプル仕訳データを投入中...");

        int insertedCount = 0;

        // 4月1日：期首仕訳（前期繰越）
        if (journalRepository.findByVoucherNumber("J25040001").isEmpty()) {
            journalRepository.save(createOpeningBalanceJournal());
            insertedCount++;
        }

        // 4月5日：売上計上（国内売上）
        if (journalRepository.findByVoucherNumber("J25040002").isEmpty()) {
            journalRepository.save(createSalesJournal());
            insertedCount++;
        }

        // 4月10日：経費計上（旅費交通費）
        if (journalRepository.findByVoucherNumber("J25040003").isEmpty()) {
            journalRepository.save(createExpenseJournal());
            insertedCount++;
        }

        // 4月15日：材料仕入
        if (journalRepository.findByVoucherNumber("J25040004").isEmpty()) {
            journalRepository.save(createPurchaseJournal());
            insertedCount++;
        }

        // 4月20日：給与支払
        if (journalRepository.findByVoucherNumber("J25040005").isEmpty()) {
            journalRepository.save(createPayrollJournal());
            insertedCount++;
        }

        // 4月25日：売掛金入金
        if (journalRepository.findByVoucherNumber("J25040006").isEmpty()) {
            journalRepository.save(createReceiptJournal());
            insertedCount++;
        }

        // 4月30日：買掛金支払
        if (journalRepository.findByVoucherNumber("J25040007").isEmpty()) {
            journalRepository.save(createPaymentJournal());
            insertedCount++;
        }

        if (log.isInfoEnabled()) {
            log.info("サンプル仕訳データ {}件 投入完了", insertedCount);
        }
    }

    /**
     * 期首仕訳（前期繰越）を作成.
     * 借方：普通預金 50,000,000 / 貸方：繰越利益剰余金 50,000,000
     */
    private Journal createOpeningBalanceJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 1);

        JournalDebitCreditDetail debit = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040001")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11130") // 普通預金
            .amount(new BigDecimal("50000000"))
            .build();

        JournalDebitCreditDetail credit = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040001")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("33200") // 繰越利益剰余金
            .amount(new BigDecimal("50000000"))
            .build();

        JournalDetail detail = JournalDetail.builder()
            .journalVoucherNumber("J25040001")
            .lineNumber(1)
            .lineSummary("前期繰越")
            .debitCreditDetails(List.of(debit, credit))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040001")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.NORMAL)
            .departmentCode("13100") // 経理部
            .details(List.of(detail))
            .build();
    }

    /**
     * 売上計上仕訳を作成.
     * Line1: 売掛金 300,000 / 国内売上高 300,000
     * Line2: 売掛金 30,000 / 仮受消費税 30,000
     */
    private Journal createSalesJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 5);

        // Line 1: 売上計上
        JournalDebitCreditDetail debitAr1 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11210") // 売掛金
            .subAccountCode("ABC001")
            .amount(new BigDecimal("300000"))
            .build();

        JournalDebitCreditDetail creditSales = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("41110") // 国内売上高
            .subAccountCode("ABC001")
            .amount(new BigDecimal("300000"))
            .taxType(TaxType.TAXABLE)
            .taxRate(10)
            .build();

        JournalDetail detail1 = JournalDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(1)
            .lineSummary("ABC商事 スキンケアセット 売上")
            .debitCreditDetails(List.of(debitAr1, creditSales))
            .build();

        // Line 2: 消費税計上
        JournalDebitCreditDetail debitAr2 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11210") // 売掛金
            .subAccountCode("ABC001")
            .amount(new BigDecimal("30000"))
            .build();

        JournalDebitCreditDetail creditTax = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("21240") // 仮受消費税
            .amount(new BigDecimal("30000"))
            .build();

        JournalDetail detail2 = JournalDetail.builder()
            .journalVoucherNumber("J25040002")
            .lineNumber(2)
            .lineSummary("ABC商事 消費税")
            .debitCreditDetails(List.of(debitAr2, creditTax))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040002")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.AUTO)
            .departmentCode("11110") // 東日本営業課
            .details(List.of(detail1, detail2))
            .build();
    }

    /**
     * 経費計上仕訳を作成.
     * Line1: 旅費交通費 27,273 / 現金 27,273
     * Line2: 仮払消費税 2,727 / 現金 2,727
     */
    private Journal createExpenseJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 10);

        // Line 1: 経費本体
        JournalDebitCreditDetail debitExpense = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("62100") // 旅費交通費
            .amount(new BigDecimal("27273"))
            .taxType(TaxType.TAXABLE)
            .taxRate(10)
            .build();

        JournalDebitCreditDetail creditCash1 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("11110") // 現金
            .amount(new BigDecimal("27273"))
            .build();

        JournalDetail detail1 = JournalDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(1)
            .lineSummary("出張旅費 東京→大阪")
            .debitCreditDetails(List.of(debitExpense, creditCash1))
            .build();

        // Line 2: 消費税
        JournalDebitCreditDetail debitTax = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11430") // 仮払消費税
            .amount(new BigDecimal("2727"))
            .build();

        JournalDebitCreditDetail creditCash2 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("11110") // 現金
            .amount(new BigDecimal("2727"))
            .build();

        JournalDetail detail2 = JournalDetail.builder()
            .journalVoucherNumber("J25040003")
            .lineNumber(2)
            .lineSummary("出張旅費 消費税")
            .debitCreditDetails(List.of(debitTax, creditCash2))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040003")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.NORMAL)
            .departmentCode("11110") // 東日本営業課
            .details(List.of(detail1, detail2))
            .build();
    }

    /**
     * 材料仕入仕訳を作成.
     * Line1: 原材料 500,000 / 買掛金 500,000
     * Line2: 仮払消費税 50,000 / 買掛金 50,000
     */
    private Journal createPurchaseJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 15);

        // Line 1: 仕入本体
        JournalDebitCreditDetail debitMaterial = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11330") // 原材料
            .subAccountCode("MAT001")
            .amount(new BigDecimal("500000"))
            .taxType(TaxType.TAXABLE)
            .taxRate(10)
            .build();

        JournalDebitCreditDetail creditAp1 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("21110") // 買掛金
            .subAccountCode("XYZ001")
            .amount(new BigDecimal("500000"))
            .build();

        JournalDetail detail1 = JournalDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(1)
            .lineSummary("XYZ化学 原材料仕入")
            .debitCreditDetails(List.of(debitMaterial, creditAp1))
            .build();

        // Line 2: 消費税
        JournalDebitCreditDetail debitTax = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11430") // 仮払消費税
            .amount(new BigDecimal("50000"))
            .build();

        JournalDebitCreditDetail creditAp2 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("21110") // 買掛金
            .subAccountCode("XYZ001")
            .amount(new BigDecimal("50000"))
            .build();

        JournalDetail detail2 = JournalDetail.builder()
            .journalVoucherNumber("J25040004")
            .lineNumber(2)
            .lineSummary("XYZ化学 消費税")
            .debitCreditDetails(List.of(debitTax, creditAp2))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040004")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.AUTO)
            .departmentCode("12110") // 第一製造課
            .details(List.of(detail1, detail2))
            .build();
    }

    /**
     * 給与支払仕訳を作成.
     * Line1: 給料手当 8,500,000 / 普通預金 8,500,000
     * Line2: 給料手当 1,500,000 / 預り金 1,500,000
     */
    private Journal createPayrollJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 20);

        // Line 1: 給与支払（手取り）
        JournalDebitCreditDetail debitPayroll1 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("61200") // 給料手当
            .amount(new BigDecimal("8500000"))
            .build();

        JournalDebitCreditDetail creditBank = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("11130") // 普通預金
            .amount(new BigDecimal("8500000"))
            .build();

        JournalDetail detail1 = JournalDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(1)
            .lineSummary("4月度給与 振込")
            .debitCreditDetails(List.of(debitPayroll1, creditBank))
            .build();

        // Line 2: 給与控除
        JournalDebitCreditDetail debitPayroll2 = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("61200") // 給料手当
            .amount(new BigDecimal("1500000"))
            .build();

        JournalDebitCreditDetail creditWithhold = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(2)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("21250") // 預り金
            .amount(new BigDecimal("1500000"))
            .build();

        JournalDetail detail2 = JournalDetail.builder()
            .journalVoucherNumber("J25040005")
            .lineNumber(2)
            .lineSummary("4月度給与 控除")
            .debitCreditDetails(List.of(debitPayroll2, creditWithhold))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040005")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.NORMAL)
            .departmentCode("13100") // 経理部
            .details(List.of(detail1, detail2))
            .build();
    }

    /**
     * 売掛金入金仕訳を作成.
     * 借方：普通預金 330,000 / 貸方：売掛金 330,000
     */
    private Journal createReceiptJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 25);

        JournalDebitCreditDetail debitBank = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040006")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("11130") // 普通預金
            .amount(new BigDecimal("330000"))
            .build();

        JournalDebitCreditDetail creditAr = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040006")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("11210") // 売掛金
            .subAccountCode("ABC001")
            .amount(new BigDecimal("330000"))
            .build();

        JournalDetail detail = JournalDetail.builder()
            .journalVoucherNumber("J25040006")
            .lineNumber(1)
            .lineSummary("ABC商事 入金")
            .debitCreditDetails(List.of(debitBank, creditAr))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040006")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.NORMAL)
            .departmentCode("13100") // 経理部
            .details(List.of(detail))
            .build();
    }

    /**
     * 買掛金支払仕訳を作成.
     * 借方：買掛金 550,000 / 貸方：普通預金 550,000
     */
    private Journal createPaymentJournal() {
        LocalDate postingDate = LocalDate.of(2025, 4, 30);

        JournalDebitCreditDetail debitAp = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040007")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.DEBIT)
            .accountCode("21110") // 買掛金
            .subAccountCode("XYZ001")
            .amount(new BigDecimal("550000"))
            .build();

        JournalDebitCreditDetail creditBank = JournalDebitCreditDetail.builder()
            .journalVoucherNumber("J25040007")
            .lineNumber(1)
            .debitCreditType(DebitCreditType.CREDIT)
            .accountCode("11130") // 普通預金
            .amount(new BigDecimal("550000"))
            .build();

        JournalDetail detail = JournalDetail.builder()
            .journalVoucherNumber("J25040007")
            .lineNumber(1)
            .lineSummary("XYZ化学 支払")
            .debitCreditDetails(List.of(debitAp, creditBank))
            .build();

        return Journal.builder()
            .journalVoucherNumber("J25040007")
            .postingDate(postingDate)
            .entryDate(postingDate)
            .closingJournalFlag(false)
            .singleEntryFlag(false)
            .voucherType(JournalVoucherType.NORMAL)
            .departmentCode("13100") // 経理部
            .details(List.of(detail))
            .build();
    }
}
