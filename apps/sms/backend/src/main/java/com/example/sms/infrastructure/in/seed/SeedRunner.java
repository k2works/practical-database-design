package com.example.sms.infrastructure.in.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * アプリケーション起動時に Seed データを投入する.
 * default プロファイルが有効な場合のみ実行される.
 */
@Component
@Profile("default")
public class SeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);

    private final SeedDataService seedDataService;

    public SeedRunner(SeedDataService seedDataService) {
        this.seedDataService = seedDataService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("default プロファイルが有効です。Seed データを投入します...");
        seedDataService.seedAll();
        log.info("Seed データの投入が完了しました。");
    }
}
