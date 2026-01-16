package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.InventoryUseCase;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.infrastructure.in.rest.dto.InventorySummaryResponse;
import com.example.pms.infrastructure.in.rest.dto.StockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 在庫 API Controller.
 */
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "inventory", description = "在庫 API")
public class InventoryController {

    private final InventoryUseCase inventoryUseCase;

    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    /**
     * 在庫一覧を取得する.
     *
     * @param itemCode 品目コード（オプション）
     * @param locationCode 場所コード（オプション）
     * @return 在庫リスト
     */
    @GetMapping
    @Operation(summary = "在庫一覧の取得")
    public ResponseEntity<List<StockResponse>> getInventory(
            @Parameter(description = "品目コード")
            @RequestParam(required = false) String itemCode,
            @Parameter(description = "場所コード")
            @RequestParam(required = false) String locationCode) {

        InventoryUseCase.InventoryQuery query = InventoryUseCase.InventoryQuery.builder()
            .itemCode(itemCode)
            .locationCode(locationCode)
            .build();

        List<Stock> stocks = inventoryUseCase.getInventory(query);
        return ResponseEntity.ok(stocks.stream()
            .map(StockResponse::from)
            .toList());
    }

    /**
     * 在庫サマリーを取得する.
     *
     * @return サマリーリスト
     */
    @GetMapping("/summary")
    @Operation(summary = "在庫サマリーの取得")
    public ResponseEntity<List<InventorySummaryResponse>> getInventorySummary() {
        List<InventoryUseCase.InventorySummary> summaries = inventoryUseCase.getInventorySummary();
        return ResponseEntity.ok(summaries.stream()
            .map(InventorySummaryResponse::from)
            .toList());
    }

    /**
     * 在庫不足品目を取得する.
     *
     * @return 在庫不足品目リスト
     */
    @GetMapping("/shortage")
    @Operation(summary = "在庫不足品目の取得")
    public ResponseEntity<List<InventorySummaryResponse>> getShortageItems() {
        List<InventoryUseCase.InventorySummary> items = inventoryUseCase.getShortageItems();
        return ResponseEntity.ok(items.stream()
            .map(InventorySummaryResponse::from)
            .toList());
    }
}
