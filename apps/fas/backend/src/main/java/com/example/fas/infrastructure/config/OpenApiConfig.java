package com.example.fas.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI（Swagger）設定クラス.
 * API ドキュメントのメタ情報を設定する.
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 設定 Bean.
     *
     * @return OpenAPI 設定
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers());
    }

    private Info apiInfo() {
        return new Info()
                .title("財務会計システム API")
                .description("財務会計システム（FAS: Financial Accounting System）の REST API")
                .version("1.0.0")
                .contact(contact())
                .license(license());
    }

    private Contact contact() {
        return new Contact()
                .name("開発チーム")
                .email("fas-dev@example.com");
    }

    private License license() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> servers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("開発サーバー");

        Server demoServer = new Server()
                .url("http://localhost:8081")
                .description("デモサーバー");

        return List.of(devServer, demoServer);
    }
}
