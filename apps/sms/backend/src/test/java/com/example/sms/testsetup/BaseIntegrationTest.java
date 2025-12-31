package com.example.sms.testsetup;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 統合テストの基底クラス。
 * TestContainers を使用して PostgreSQL コンテナを起動し、
 * Spring Boot の @ServiceConnection で自動的にデータソースを設定する。
 * Flyway マイグレーションが自動実行される。
 */
@Testcontainers
@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod", "PMD.MutableStaticState"})
public abstract class BaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    /**
     * 継承のみを許可するための protected コンストラクタ。
     */
    protected BaseIntegrationTest() {
        // TestContainers の基底クラスのため空実装
    }
}
