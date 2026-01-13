package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.domain.model.item.Item;
import com.example.pms.infrastructure.in.rest.dto.CreateItemRequest;
import com.example.pms.infrastructure.in.rest.dto.ItemResponse;
import com.example.pms.infrastructure.in.rest.dto.UpdateItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 品目マスタ API Controller.
 */
@RestController
@RequestMapping("/api/items")
@Tag(name = "items", description = "品目マスタ API")
public class ItemController {

    private final ItemUseCase itemUseCase;

    public ItemController(ItemUseCase itemUseCase) {
        this.itemUseCase = itemUseCase;
    }

    /**
     * 品目一覧を取得する.
     *
     * @return 品目リスト
     */
    @GetMapping
    @Operation(summary = "品目一覧の取得")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<Item> items = itemUseCase.getAllItems();
        return ResponseEntity.ok(items.stream()
            .map(ItemResponse::from)
            .toList());
    }

    /**
     * 品目を取得する.
     *
     * @param itemCode 品目コード
     * @return 品目
     */
    @GetMapping("/{itemCode}")
    @Operation(summary = "品目の取得")
    public ResponseEntity<ItemResponse> getItem(@PathVariable String itemCode) {
        Item item = itemUseCase.getItem(itemCode);
        return ResponseEntity.ok(ItemResponse.from(item));
    }

    /**
     * 品目を登録する.
     *
     * @param request 登録リクエスト
     * @return 登録した品目
     */
    @PostMapping
    @Operation(summary = "品目の登録")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        Item item = itemUseCase.createItem(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ItemResponse.from(item));
    }

    /**
     * 品目を更新する.
     *
     * @param itemCode 品目コード
     * @param request 更新リクエスト
     * @return 更新した品目
     */
    @PutMapping("/{itemCode}")
    @Operation(summary = "品目の更新")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable String itemCode,
            @Valid @RequestBody UpdateItemRequest request) {
        Item item = itemUseCase.updateItem(itemCode, request.toCommand());
        return ResponseEntity.ok(ItemResponse.from(item));
    }

    /**
     * 品目を削除する.
     *
     * @param itemCode 品目コード
     */
    @DeleteMapping("/{itemCode}")
    @Operation(summary = "品目の削除")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable String itemCode) {
        itemUseCase.deleteItem(itemCode);
    }
}
