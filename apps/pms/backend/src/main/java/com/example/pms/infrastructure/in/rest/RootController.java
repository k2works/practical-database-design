package com.example.pms.infrastructure.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * API ルート Controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "root", description = "API ルート")
public class RootController {

    /**
     * API 情報を取得.
     *
     * @return API 情報
     */
    @GetMapping
    @Operation(summary = "API 情報の取得")
    public ResponseEntity<Map<String, String>> root() {
        return ResponseEntity.ok(Map.of(
            "service", "Production Management API",
            "version", "1.0",
            "docs", "/swagger-ui.html"
        ));
    }

    /**
     * ヘルスチェック.
     *
     * @return ヘルス状態
     */
    @GetMapping("/health")
    @Operation(summary = "ヘルスチェック")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
