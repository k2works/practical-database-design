package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import com.example.pms.infrastructure.in.rest.dto.CreatePurchaseOrderRequest;
import com.example.pms.infrastructure.in.rest.dto.PurchaseOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 発注 API Controller.
 */
@RestController
@RequestMapping("/api/purchase-orders")
@Tag(name = "purchase-orders", description = "発注 API")
public class PurchaseOrderController {

    private final PurchaseOrderUseCase purchaseOrderUseCase;

    public PurchaseOrderController(PurchaseOrderUseCase purchaseOrderUseCase) {
        this.purchaseOrderUseCase = purchaseOrderUseCase;
    }

    /**
     * 発注一覧を取得する.
     *
     * @param status ステータス（オプション）
     * @return 発注リスト
     */
    @GetMapping
    @Operation(summary = "発注一覧の取得")
    public ResponseEntity<List<PurchaseOrderResponse>> getAllOrders(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) PurchaseOrderStatus status) {
        List<PurchaseOrder> orders;
        if (status != null) {
            orders = purchaseOrderUseCase.getOrdersByStatus(status);
        } else {
            orders = purchaseOrderUseCase.getAllOrders();
        }
        return ResponseEntity.ok(orders.stream()
            .map(PurchaseOrderResponse::from)
            .toList());
    }

    /**
     * 発注を取得する.
     *
     * @param orderNumber 発注番号
     * @return 発注
     */
    @GetMapping("/{orderNumber}")
    @Operation(summary = "発注詳細の取得")
    public ResponseEntity<PurchaseOrderResponse> getOrder(@PathVariable String orderNumber) {
        PurchaseOrder order = purchaseOrderUseCase.getOrder(orderNumber);
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }

    /**
     * 発注を登録する.
     *
     * @param request 登録リクエスト
     * @return 登録した発注
     */
    @PostMapping
    @Operation(summary = "発注の登録")
    public ResponseEntity<PurchaseOrderResponse> createOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        PurchaseOrder order = purchaseOrderUseCase.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PurchaseOrderResponse.from(order));
    }

    /**
     * 発注を確定する.
     *
     * @param orderNumber 発注番号
     * @return 確定した発注
     */
    @PostMapping("/{orderNumber}/confirm")
    @Operation(summary = "発注の確定")
    public ResponseEntity<PurchaseOrderResponse> confirmOrder(@PathVariable String orderNumber) {
        PurchaseOrder order = purchaseOrderUseCase.confirmOrder(orderNumber);
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }

    /**
     * 発注を取消する.
     *
     * @param orderNumber 発注番号
     */
    @DeleteMapping("/{orderNumber}")
    @Operation(summary = "発注の取消")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable String orderNumber) {
        purchaseOrderUseCase.cancelOrder(orderNumber);
    }
}
