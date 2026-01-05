package com.example.sms.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 設定.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("販売管理システム API")
                .description("TDD で育てる販売管理システムの API ドキュメント")
                .version("1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("開発サーバー")))
            .tags(List.of(
                new Tag().name("products").description("商品マスタ API"),
                new Tag().name("partners").description("取引先マスタ API"),
                new Tag().name("customers").description("顧客 API"),
                new Tag().name("orders").description("受注 API"),
                new Tag().name("shipments").description("出荷 API"),
                new Tag().name("sales").description("売上 API"),
                new Tag().name("invoices").description("請求 API"),
                new Tag().name("receipts").description("入金 API")));
    }
}
