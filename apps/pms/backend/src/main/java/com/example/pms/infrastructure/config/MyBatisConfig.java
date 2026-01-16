package com.example.pms.infrastructure.config;

import java.util.Properties;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 設定クラス.
 * データベースベンダーに応じた SQL 切り替えを可能にする databaseIdProvider を提供する.
 */
@Configuration
public class MyBatisConfig {

    /**
     * データベースベンダー識別プロバイダー.
     * MyBatis のマッパー XML で databaseId 属性を使用して、
     * データベースベンダーごとに異なる SQL を定義できるようにする.
     *
     * @return VendorDatabaseIdProvider
     */
    @Bean
    public VendorDatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("H2", "h2");
        provider.setProperties(properties);
        return provider;
    }
}
