package com.example.sms.testsetup;

import com.example.sms.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 統合テストの基底クラス。
 * TestContainers を使用して PostgreSQL コンテナを起動し、
 * Spring Boot の @ServiceConnection で自動的にデータソースを設定する。
 * Flyway マイグレーションが自動実行される。
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod"})
public abstract class BaseIntegrationTest {

    /**
     * 継承のみを許可するための protected コンストラクタ。
     */
    protected BaseIntegrationTest() {
        // TestContainers の基底クラスのため空実装
    }
}
