package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.application.port.in.command.CreateShipmentCommand;
import com.example.sms.application.port.in.command.UpdateShipmentCommand;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateShipmentRequest;
import com.example.sms.infrastructure.in.rest.dto.ShipmentResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateShipmentRequest;
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
 * 出荷 API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "shipments", description = "出荷 API")
public class ShipmentController {

    private final ShipmentUseCase shipmentUseCase;

    public ShipmentController(ShipmentUseCase shipmentUseCase) {
        this.shipmentUseCase = shipmentUseCase;
    }

    @GetMapping
    @Operation(summary = "出荷一覧の取得", description = "すべての出荷を取得します")
    @ApiResponse(responseCode = "200", description = "出荷一覧を返却")
    public ResponseEntity<List<ShipmentResponse>> getAllShipments(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) ShipmentStatus status,
            @Parameter(description = "受注IDでフィルタ")
            @RequestParam(required = false) Integer orderId) {

        List<Shipment> shipments;
        if (status != null) {
            shipments = shipmentUseCase.getShipmentsByStatus(status);
        } else if (orderId != null) {
            shipments = shipmentUseCase.getShipmentsByOrder(orderId);
        } else {
            shipments = shipmentUseCase.getAllShipments();
        }

        List<ShipmentResponse> responses = shipments.stream()
            .map(ShipmentResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{shipmentNumber}")
    @Operation(summary = "出荷の取得", description = "出荷番号を指定して出荷情報を取得します")
    @ApiResponse(responseCode = "200", description = "出荷を返却")
    @ApiResponse(responseCode = "404", description = "出荷が見つからない")
    public ResponseEntity<ShipmentResponse> getShipment(
            @Parameter(description = "出荷番号", example = "SHP-20250101-0001")
            @PathVariable String shipmentNumber) {

        Shipment shipment = shipmentUseCase.getShipmentByNumber(shipmentNumber);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    @PostMapping
    @Operation(summary = "出荷の登録", description = "新規出荷を登録します")
    @ApiResponse(responseCode = "201", description = "出荷を登録")
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {

        List<CreateShipmentCommand.CreateShipmentDetailCommand> detailCommands = request.details().stream()
            .map(d -> new CreateShipmentCommand.CreateShipmentDetailCommand(
                d.orderDetailId(),
                d.productCode(),
                d.productName(),
                d.shippedQuantity(),
                d.unit(),
                d.unitPrice(),
                d.warehouseCode(),
                d.remarks()
            ))
            .toList();

        CreateShipmentCommand command = new CreateShipmentCommand(
            request.shipmentDate(),
            request.orderId(),
            request.customerCode(),
            request.customerBranchNumber(),
            request.shippingDestinationNumber(),
            request.shippingDestinationName(),
            request.shippingDestinationPostalCode(),
            request.shippingDestinationAddress1(),
            request.shippingDestinationAddress2(),
            request.representativeCode(),
            request.warehouseCode(),
            request.remarks(),
            detailCommands
        );

        Shipment shipment = shipmentUseCase.createShipment(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ShipmentResponse.from(shipment));
    }

    @PutMapping("/{shipmentNumber}")
    @Operation(summary = "出荷の更新", description = "出荷番号を指定して出荷情報を更新します")
    @ApiResponse(responseCode = "200", description = "出荷を更新")
    @ApiResponse(responseCode = "404", description = "出荷が見つからない")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @Parameter(description = "出荷番号", example = "SHP-20250101-0001")
            @PathVariable String shipmentNumber,
            @Valid @RequestBody UpdateShipmentRequest request) {

        UpdateShipmentCommand command = new UpdateShipmentCommand(
            request.status(),
            request.remarks()
        );

        Shipment shipment = shipmentUseCase.updateShipment(shipmentNumber, command);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    @PostMapping("/{shipmentNumber}/confirm")
    @Operation(summary = "出荷の確定", description = "出荷番号を指定して出荷を確定します")
    @ApiResponse(responseCode = "200", description = "出荷を確定")
    @ApiResponse(responseCode = "404", description = "出荷が見つからない")
    public ResponseEntity<ShipmentResponse> confirmShipment(
            @Parameter(description = "出荷番号", example = "SHP-20250101-0001")
            @PathVariable String shipmentNumber) {

        Shipment shipment = shipmentUseCase.confirmShipment(shipmentNumber);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    @DeleteMapping("/{shipmentNumber}")
    @Operation(summary = "出荷の削除", description = "出荷番号を指定して出荷を削除します")
    @ApiResponse(responseCode = "204", description = "出荷を削除")
    @ApiResponse(responseCode = "404", description = "出荷が見つからない")
    public ResponseEntity<Void> deleteShipment(
            @Parameter(description = "出荷番号", example = "SHP-20250101-0001")
            @PathVariable String shipmentNumber) {

        shipmentUseCase.deleteShipment(shipmentNumber);
        return ResponseEntity.noContent().build();
    }
}
