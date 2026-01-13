package com.example.pms.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger 設定.
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 設定を作成.
     *
     * @return OpenAPI 設定
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("生産管理 API")
                .version("1.0")
                .description("生産管理システムの RESTful API")
                .contact(new Contact()
                    .name("開発チーム")
                    .email("dev@example.com")));
    }
}
