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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed データ投入サービス.
 * 冪等性を保証し、既存データがある場合はスキップする.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeedDataService {

    private final TaxTransactionRepository taxTransactionRepository;
    private final AccountRepository accountRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * 全 Seed データを投入.
     */
    @Transactional
    public void seedAll() {
        if (log.isInfoEnabled()) {
            log.info("Starting seed data insertion...");
        }

        seedTaxTransactions();
        seedAccounts();
        seedDepartments();

        if (log.isInfoEnabled()) {
            log.info("Seed data insertion completed.");
        }
    }

    /**
     * 課税取引マスタの Seed データを投入.
     */
    @Transactional
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void seedTaxTransactions() {
        if (log.isInfoEnabled()) {
            log.info("Seeding tax transactions...");
        }

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
            log.info("Tax transactions seeded: {} records", insertedCount);
        }
    }

    /**
     * 勘定科目マスタの Seed データを投入.
     */
    @Transactional
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void seedAccounts() {
        if (log.isInfoEnabled()) {
            log.info("Seeding accounts...");
        }

        List<Account> accounts = List.of(
            // 資産科目
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
            Account.builder()
                .accountCode("11210").accountName("売掛金").accountShortName("売掛金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING).build(),
            // 負債科目
            Account.builder()
                .accountCode("21110").accountName("買掛金").accountShortName("買掛金")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.LIABILITY)
                .aggregationType(AggregationType.POSTING).build(),
            // 純資産科目
            Account.builder()
                .accountCode("33200").accountName("繰越利益剰余金").accountShortName("繰越利益")
                .bsplType(BSPLType.BS).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.EQUITY)
                .aggregationType(AggregationType.POSTING).build(),
            // 収益科目
            Account.builder()
                .accountCode("41110").accountName("国内売上高").accountShortName("国内売上")
                .bsplType(BSPLType.PL).debitCreditType(DebitCreditType.CREDIT)
                .transactionElementType(TransactionElementType.REVENUE)
                .aggregationType(AggregationType.POSTING).build(),
            // 費用科目
            Account.builder()
                .accountCode("62100").accountName("旅費交通費").accountShortName("旅費交通")
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
            log.info("Accounts seeded: {} records", insertedCount);
        }
    }

    /**
     * 部門マスタの Seed データを投入.
     */
    @Transactional
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void seedDepartments() {
        if (log.isInfoEnabled()) {
            log.info("Seeding departments...");
        }

        List<Department> departments = List.of(
            Department.builder()
                .departmentCode("10000").departmentName("全社").departmentShortName("全社")
                .organizationLevel(0).departmentPath("10000").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("11000").departmentName("営業本部").departmentShortName("営業")
                .organizationLevel(1).departmentPath("10000~11000").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("11110").departmentName("東日本営業課").departmentShortName("東日本営業")
                .organizationLevel(3).departmentPath("10000~11000~11100~11110").lowestLevelFlag(1)
                .build(),
            Department.builder()
                .departmentCode("12000").departmentName("製造本部").departmentShortName("製造")
                .organizationLevel(1).departmentPath("10000~12000").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("12110").departmentName("第一製造課").departmentShortName("一製課")
                .organizationLevel(3).departmentPath("10000~12000~12100~12110").lowestLevelFlag(1)
                .build(),
            Department.builder()
                .departmentCode("13000").departmentName("管理本部").departmentShortName("管理")
                .organizationLevel(1).departmentPath("10000~13000").lowestLevelFlag(0).build(),
            Department.builder()
                .departmentCode("13100").departmentName("経理部").departmentShortName("経理")
                .organizationLevel(2).departmentPath("10000~13000~13100").lowestLevelFlag(0).build()
        );

        int insertedCount = 0;
        for (Department dept : departments) {
            if (departmentRepository.findByCode(dept.getDepartmentCode()).isEmpty()) {
                departmentRepository.save(dept);
                insertedCount++;
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Departments seeded: {} records", insertedCount);
        }
    }
}
