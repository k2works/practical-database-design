package com.example.fas.infrastructure.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * アプリケーション起動時に Seed データを投入する Runner.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataRunner implements ApplicationRunner {

    private final SeedDataService seedDataService;

    @Override
    public void run(ApplicationArguments args) {
        if (log.isInfoEnabled()) {
            log.info("Running seed data initialization...");
        }
        seedDataService.seedAll();
    }
}
