package com.example.sms.infrastructure.in.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Seed データ投入サービス.
 * B社事例に基づく販売管理システムの初期データを投入する。
 */
@Service
public class SeedDataService {

    private static final Logger LOG = LoggerFactory.getLogger(SeedDataService.class);

    private final MasterDataSeeder masterDataSeeder;
    private final TransactionDataSeeder transactionDataSeeder;

    public SeedDataService(
            MasterDataSeeder masterDataSeeder,
            TransactionDataSeeder transactionDataSeeder) {
        this.masterDataSeeder = masterDataSeeder;
        this.transactionDataSeeder = transactionDataSeeder;
    }

    /**
     * すべての Seed データを投入.
     */
    @Transactional
    public void seedAll() {
        LOG.info("========================================");
        LOG.info("販売管理システム Seed データ投入開始");
        LOG.info("========================================");

        LocalDate effectiveDate = LocalDate.of(2025, 1, 1);

        // 既存データの削除
        cleanAllData();

        // マスタデータの投入
        masterDataSeeder.seedAll(effectiveDate);

        // トランザクションデータの投入
        transactionDataSeeder.seedAll(effectiveDate);

        LOG.info("========================================");
        LOG.info("販売管理システム Seed データ投入完了!");
        LOG.info("========================================");
    }

    /**
     * マスタデータのみ投入.
     */
    @Transactional
    public void seedMasterDataOnly() {
        LOG.info("マスタデータのみ投入開始");

        LocalDate effectiveDate = LocalDate.of(2025, 1, 1);
        cleanAllData();
        masterDataSeeder.seedAll(effectiveDate);

        LOG.info("マスタデータ投入完了");
    }

    private void cleanAllData() {
        LOG.info("既存データを削除中...");

        // トランザクションデータから削除（外部キー制約のため逆順）
        transactionDataSeeder.cleanAll();

        // マスタデータを削除
        masterDataSeeder.cleanAll();

        LOG.info("既存データ削除完了");
    }
}
