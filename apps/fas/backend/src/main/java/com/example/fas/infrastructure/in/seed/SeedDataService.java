package com.example.fas.infrastructure.in.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed データ投入サービス.
 * 財務会計システムの初期データを投入する。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeedDataService {

    private final MasterDataSeeder masterDataSeeder;
    private final TransactionDataSeeder transactionDataSeeder;

    /**
     * すべての Seed データを投入.
     */
    @Transactional
    public void seedAll() {
        log.info("========================================");
        log.info("財務会計システム Seed データ投入開始");
        log.info("========================================");

        // マスタデータの投入
        masterDataSeeder.seedAll();

        // トランザクションデータの投入
        transactionDataSeeder.seedAll();

        log.info("========================================");
        log.info("財務会計システム Seed データ投入完了!");
        log.info("========================================");
    }

    /**
     * マスタデータのみ投入.
     */
    @Transactional
    public void seedMasterDataOnly() {
        log.info("マスタデータのみ投入開始");

        masterDataSeeder.seedAll();

        log.info("マスタデータ投入完了");
    }

    /**
     * すべてのデータを削除.
     */
    @Transactional
    public void cleanAllData() {
        log.info("既存データを削除中...");

        // トランザクションデータから削除（外部キー制約のため逆順）
        transactionDataSeeder.cleanAll();

        // マスタデータを削除
        masterDataSeeder.cleanAll();

        log.info("既存データ削除完了");
    }
}
