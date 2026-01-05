package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.command.CreateOrderCommand;
import com.example.sms.application.port.in.command.UpdateOrderCommand;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.infrastructure.in.rest.dto.CreateOrderRequest;
import com.example.sms.infrastructure.in.rest.dto.OrderDetailResponse;
import com.example.sms.infrastructure.in.rest.dto.OrderResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 受注 API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "orders", description = "受注 API")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @GetMapping
    @Operation(summary = "受注一覧の取得", description = "すべての受注を取得します")
    @ApiResponse(responseCode = "200", description = "受注一覧を返却")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "顧客コードでフィルタ")
            @RequestParam(required = false) String customerCode) {

        List<SalesOrder> orders;
        if (status != null) {
            orders = orderUseCase.getOrdersByStatus(status);
        } else if (customerCode != null) {
            orders = orderUseCase.getOrdersByCustomer(customerCode);
        } else {
            orders = orderUseCase.getAllOrders();
        }

        List<OrderResponse> responses = orders.stream()
            .map(OrderResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "受注の取得", description = "受注番号を指定して受注情報を取得します")
    @ApiResponse(responseCode = "200", description = "受注を返却")
    @ApiResponse(responseCode = "404", description = "受注が見つからない")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "受注番号", example = "ORD-20250101-0001")
            @PathVariable String orderNumber) {

        SalesOrder order = orderUseCase.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{orderNumber}/details")
    @Operation(summary = "受注明細の取得", description = "受注番号を指定して受注明細を取得します")
    @ApiResponse(responseCode = "200", description = "受注明細を返却")
    @ApiResponse(responseCode = "404", description = "受注が見つからない")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetails(
            @Parameter(description = "受注番号", example = "ORD-20250101-0001")
            @PathVariable String orderNumber) {

        SalesOrder order = orderUseCase.getOrderWithDetails(orderNumber);
        List<OrderDetailResponse> responses = order.getDetails().stream()
            .map(OrderDetailResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "受注の登録", description = "新規受注を登録します")
    @ApiResponse(responseCode = "201", description = "受注を登録")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        List<CreateOrderCommand.CreateOrderDetailCommand> detailCommands = request.details().stream()
            .map(d -> new CreateOrderCommand.CreateOrderDetailCommand(
                d.productCode(),
                d.productName(),
                d.orderQuantity(),
                d.unit(),
                d.unitPrice(),
                d.warehouseCode(),
                d.requestedDeliveryDate(),
                d.remarks()
            ))
            .toList();

        CreateOrderCommand command = new CreateOrderCommand(
            request.orderDate(),
            request.customerCode(),
            request.customerBranchNumber(),
            request.shippingDestinationNumber(),
            request.representativeCode(),
            request.requestedDeliveryDate(),
            request.scheduledShippingDate(),
            request.quotationId(),
            request.customerOrderNumber(),
            request.remarks(),
            detailCommands
        );

        SalesOrder order = orderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(order));
    }

    @PutMapping("/{orderNumber}")
    @Operation(summary = "受注の更新", description = "受注番号を指定して受注情報を更新します")
    @ApiResponse(responseCode = "200", description = "受注を更新")
    @ApiResponse(responseCode = "404", description = "受注が見つからない")
    @ApiResponse(responseCode = "409", description = "楽観ロックエラー")
    public ResponseEntity<OrderResponse> updateOrder(
            @Parameter(description = "受注番号", example = "ORD-20250101-0001")
            @PathVariable String orderNumber,
            @Valid @RequestBody UpdateOrderRequest request) {

        UpdateOrderCommand command = new UpdateOrderCommand(
            request.shippingDestinationNumber(),
            request.representativeCode(),
            request.requestedDeliveryDate(),
            request.scheduledShippingDate(),
            request.status(),
            request.customerOrderNumber(),
            request.remarks(),
            request.version()
        );

        SalesOrder order = orderUseCase.updateOrder(orderNumber, command);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("/{orderNumber}/cancel")
    @Operation(summary = "受注のキャンセル", description = "受注番号を指定して受注をキャンセルします")
    @ApiResponse(responseCode = "200", description = "受注をキャンセル")
    @ApiResponse(responseCode = "404", description = "受注が見つからない")
    @ApiResponse(responseCode = "409", description = "楽観ロックエラー")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "受注番号", example = "ORD-20250101-0001")
            @PathVariable String orderNumber,
            @Parameter(description = "楽観ロック用バージョン")
            @RequestParam(required = false) Integer version) {

        SalesOrder order = orderUseCase.cancelOrder(orderNumber, version);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @DeleteMapping("/{orderNumber}")
    @Operation(summary = "受注の削除", description = "受注番号を指定して受注を削除します")
    @ApiResponse(responseCode = "204", description = "受注を削除")
    @ApiResponse(responseCode = "404", description = "受注が見つからない")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "受注番号", example = "ORD-20250101-0001")
            @PathVariable String orderNumber) {

        orderUseCase.deleteOrder(orderNumber);
        return ResponseEntity.noContent().build();
    }
}
