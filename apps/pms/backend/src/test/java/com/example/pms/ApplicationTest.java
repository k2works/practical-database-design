package com.example.pms;

import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * アプリケーションの起動テスト.
 */
class ApplicationTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // アプリケーションコンテキストがロードされることを確認
        assertNotNull(applicationContext, "ApplicationContext should not be null");
    }
}
