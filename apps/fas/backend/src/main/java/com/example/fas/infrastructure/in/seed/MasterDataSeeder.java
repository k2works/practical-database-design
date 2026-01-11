package com.example.fas.infrastructure.in.seed;

import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.application.port.out.DepartmentRepository;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
import com.example.fas.domain.model.department.Department;
import com.example.fas.domain.model.tax.TaxTransaction;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * マスタデータ Seeder.
 * D社事例（化粧品製造販売会社）に基づくマスタデータを投入する。
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
public class MasterDataSeeder {

    private final TaxTransactionRepository taxTransactionRepository;
    private final AccountRepository accountRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * すべてのマスタデータを投入.
     */
    public void seedAll() {
        seedTaxTransactions();
        seedAccounts();
        seedDepartments();
    }

    /**
     * すべてのマスタデータを削除.
     */
    public void cleanAll() {
        departmentRepository.deleteAll();
        accountRepository.deleteAll();
        taxTransactionRepository.deleteAll();
    }

    /**
     * 課税取引マスタの Seed データを投入.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedTaxTransactions() {
        log.info("課税取引マスタを投入中...");

        List<TaxTransaction> taxes = List.of(
            TaxTransaction.builder()
                .taxCode("00").taxName("免税").taxRate(BigDecimal.ZERO).build(),
            TaxTransaction.builder()
                .taxCode("08").taxName("軽減税率8%").taxRate(new BigDecimal("0.080")).build(),
            TaxTransaction.builder()
                .taxCode("10").taxName("標準税率10%").taxRate(new BigDecimal("0.100")).build(),
            TaxTransaction.builder()
                .taxCode("80").taxName("非課税").taxRate(BigDecimal.ZERO).build(),
            TaxTransaction.builder()
                .taxCode("99").taxName("対象外").taxRate(BigDecimal.ZERO).build()
        );

        int insertedCount = 0;
        for (TaxTransaction tax : taxes) {
            if (taxTransactionRepository.findByCode(tax.getTaxCode()).isEmpty()) {
                taxTransactionRepository.save(tax);
                insertedCount++;
            }
        }

        if (log.isInfoEnabled()) {
            log.info("課税取引マスタ {}件 投入完了", insertedCount);
        }
    }

    /**
     * 勘定科目マスタの Seed データを投入.
     * D社事例に基づく勘定科目体系。
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedAccounts() {
        log.info("勘定科目マスタを投入中...");

        List<Account> accounts = List.of(
            // ========================================
            // 資産科目（BS借方）
            // ========================================
            // 流動資産 - 現金預金
            Account.builder()
                .accountCode("11110").accountName("現金").accountShortName("現金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),
            Account.builder()
                .accountCode("11130").accountName("普通預金").accountShortName("普通")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),
            // 流動資産 - 売上債権
            Account.builder()
                .accountCode("11210").accountName("売掛金").accountShortName("売掛金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),
            // 流動資産 - 棚卸資産
            Account.builder()
                .accountCode("11330").accountName("原材料").accountShortName("原材料")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),
            // 流動資産 - その他流動資産
            Account.builder()
                .accountCode("11430").accountName("仮払消費税").accountShortName("仮払税")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),

            // ========================================
            // 負債科目（BS貸方）
            // ========================================
            // 流動負債 - 仕入債務
            Account.builder()
                .accountCode("21110").accountName("買掛金").accountShortName("買掛金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.LIABILITY)
                .aggregationType(AggregationType.POSTING).build(),
            // 流動負債 - その他流動負債
            Account.builder()
                .accountCode("21240").accountName("仮受消費税").accountShortName("仮受税")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.LIABILITY)
                .aggregationType(AggregationType.POSTING).build(),
            Account.builder()
                .accountCode("21250").accountName("預り金").accountShortName("預り金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.LIABILITY)
                .aggregationType(AggregationType.POSTING).build(),

            // ========================================
            // 純資産科目（BS貸方）
            // ========================================
            Account.builder()
                .accountCode("31100").accountName("資本金").accountShortName("資本金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.EQUITY)
                .aggregationType(AggregationType.POSTING).build(),
            Account.builder()
                .accountCode("33200").accountName("繰越利益剰余金").accountShortName("繰越利益")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.EQUITY)
                .aggregationType(AggregationType.POSTING).build(),

            // ========================================
            // 収益科目（PL貸方）
            // ========================================
            Account.builder()
                .accountCode("41110").accountName("国内売上高").accountShortName("国内売上")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.REVENUE)
                .aggregationType(AggregationType.POSTING).build(),
            Account.builder()
                .accountCode("41120").accountName("輸出売上高").accountShortName("輸出売上")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.REVENUE)
                .aggregationType(AggregationType.POSTING).build(),

            // ========================================
            // 費用科目（PL借方）
            // ========================================
            // 製造原価
            Account.builder()
                .accountCode("51100").accountName("材料費").accountShortName("材料費")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.EXPENSE)
                .aggregationType(AggregationType.POSTING).build(),
            // 販管費 - 人件費
            Account.builder()
                .accountCode("61200").accountName("給料手当").accountShortName("給料")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.EXPENSE)
                .aggregationType(AggregationType.POSTING).build(),
            // 販管費 - 旅費交通費
            Account.builder()
                .accountCode("62100").accountName("旅費交通費").accountShortName("旅費交通")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.EXPENSE)
                .aggregationType(AggregationType.POSTING).build(),
            // 販管費 - 広告宣伝費
            Account.builder()
                .accountCode("63100").accountName("広告宣伝費").accountShortName("広告宣伝")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.EXPENSE)
                .aggregationType(AggregationType.POSTING).build()
        );

        int insertedCount = 0;
        for (Account account : accounts) {
            if (accountRepository.findByCode(account.getAccountCode()).isEmpty()) {
                accountRepository.save(account);
                insertedCount++;
            }
        }

        if (log.isInfoEnabled()) {
            log.info("勘定科目マスタ {}件 投入完了", insertedCount);
        }
    }

    /**
     * 部門マスタの Seed データを投入.
     * D社事例に基づく組織構成（本部-部-課の3階層）。
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void seedDepartments() {
        log.info("部門マスタを投入中...");

        List<Department> departments = List.of(
            // ========================================
            // 全社（レベル0）
            // ========================================
            Department.builder()
                .departmentCode("10000").departmentName("全社").departmentShortName("全社")
                .organizationLevel(0).departmentPath("10000").lowestLevelFlag(0).build(),

            // ========================================
            // 営業本部配下
            // ========================================
            Department.builder()
                .departmentCode("11000").departmentName("営業本部").departmentShortName("営業")
                .organizationLevel(1).departmentPath("10000~11000").lowestLevelFlag(0).build(),
            // 東日本営業部
            Department.builder()
                .departmentCode("11100").departmentName("東日本営業部").departmentShortName("東日本")
                .organizationLevel(2).departmentPath("10000~11000~11100").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("11110").departmentName("東日本営業課").departmentShortName("東日本営業")
                .organizationLevel(3).departmentPath("10000~11000~11100~11110").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("11120").departmentName("東日本企画課").departmentShortName("東日本企画")
                .organizationLevel(3).departmentPath("10000~11000~11100~11120").lowestLevelFlag(1).build(),
            // 西日本営業部
            Department.builder()
                .departmentCode("11200").departmentName("西日本営業部").departmentShortName("西日本")
                .organizationLevel(2).departmentPath("10000~11000~11200").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("11210").departmentName("西日本営業課").departmentShortName("西日本営業")
                .organizationLevel(3).departmentPath("10000~11000~11200~11210").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("11220").departmentName("西日本企画課").departmentShortName("西日本企画")
                .organizationLevel(3).departmentPath("10000~11000~11200~11220").lowestLevelFlag(1).build(),
            // 海外営業部
            Department.builder()
                .departmentCode("11300").departmentName("海外営業部").departmentShortName("海外")
                .organizationLevel(2).departmentPath("10000~11000~11300").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("11310").departmentName("アジア営業課").departmentShortName("アジア営業")
                .organizationLevel(3).departmentPath("10000~11000~11300~11310").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("11320").departmentName("欧米営業課").departmentShortName("欧米営業")
                .organizationLevel(3).departmentPath("10000~11000~11300~11320").lowestLevelFlag(1).build(),

            // ========================================
            // 製造本部配下
            // ========================================
            Department.builder()
                .departmentCode("12000").departmentName("製造本部").departmentShortName("製造")
                .organizationLevel(1).departmentPath("10000~12000").lowestLevelFlag(0).build(),
            // 第一製造部
            Department.builder()
                .departmentCode("12100").departmentName("第一製造部").departmentShortName("第一製造")
                .organizationLevel(2).departmentPath("10000~12000~12100").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("12110").departmentName("第一製造課").departmentShortName("一製課")
                .organizationLevel(3).departmentPath("10000~12000~12100~12110").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("12120").departmentName("第一検査課").departmentShortName("一検課")
                .organizationLevel(3).departmentPath("10000~12000~12100~12120").lowestLevelFlag(1).build(),
            // 第二製造部
            Department.builder()
                .departmentCode("12200").departmentName("第二製造部").departmentShortName("第二製造")
                .organizationLevel(2).departmentPath("10000~12000~12200").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("12210").departmentName("第二製造課").departmentShortName("二製課")
                .organizationLevel(3).departmentPath("10000~12000~12200~12210").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("12220").departmentName("第二検査課").departmentShortName("二検課")
                .organizationLevel(3).departmentPath("10000~12000~12200~12220").lowestLevelFlag(1).build(),
            // 品質管理部
            Department.builder()
                .departmentCode("12300").departmentName("品質管理部").departmentShortName("品管")
                .organizationLevel(2).departmentPath("10000~12000~12300").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("12310").departmentName("品質管理課").departmentShortName("品管課")
                .organizationLevel(3).departmentPath("10000~12000~12300~12310").lowestLevelFlag(1).build(),

            // ========================================
            // 管理本部配下
            // ========================================
            Department.builder()
                .departmentCode("13000").departmentName("管理本部").departmentShortName("管理")
                .organizationLevel(1).departmentPath("10000~13000").lowestLevelFlag(0).build(),
            // 経理部
            Department.builder()
                .departmentCode("13100").departmentName("経理部").departmentShortName("経理")
                .organizationLevel(2).departmentPath("10000~13000~13100").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("13110").departmentName("経理課").departmentShortName("経理課")
                .organizationLevel(3).departmentPath("10000~13000~13100~13110").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("13120").departmentName("財務課").departmentShortName("財務課")
                .organizationLevel(3).departmentPath("10000~13000~13100~13120").lowestLevelFlag(1).build(),
            // 人事部
            Department.builder()
                .departmentCode("13200").departmentName("人事部").departmentShortName("人事")
                .organizationLevel(2).departmentPath("10000~13000~13200").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("13210").departmentName("人事課").departmentShortName("人事課")
                .organizationLevel(3).departmentPath("10000~13000~13200~13210").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("13220").departmentName("労務課").departmentShortName("労務課")
                .organizationLevel(3).departmentPath("10000~13000~13200~13220").lowestLevelFlag(1).build(),
            // 総務部
            Department.builder()
                .departmentCode("13300").departmentName("総務部").departmentShortName("総務")
                .organizationLevel(2).departmentPath("10000~13000~13300").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("13310").departmentName("総務課").departmentShortName("総務課")
                .organizationLevel(3).departmentPath("10000~13000~13300~13310").lowestLevelFlag(1).build(),
            Department.builder()
                .departmentCode("13320").departmentName("法務課").departmentShortName("法務課")
                .organizationLevel(3).departmentPath("10000~13000~13300~13320").lowestLevelFlag(1).build()
        );

        int insertedCount = 0;
        for (Department dept : departments) {
            if (departmentRepository.findByCode(dept.getDepartmentCode()).isEmpty()) {
                departmentRepository.save(dept);
                insertedCount++;
            }
        }

        if (log.isInfoEnabled()) {
            log.info("部門マスタ {}件 投入完了", insertedCount);
        }
    }
}
