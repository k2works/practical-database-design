package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.SalesUseCase;
import com.example.sms.application.port.in.command.CreateSalesCommand;
import com.example.sms.application.port.in.command.UpdateSalesCommand;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateSalesRequest;
import com.example.sms.infrastructure.in.rest.dto.SalesResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateSalesRequest;
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
 * 売上 API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "sales", description = "売上 API")
public class SalesController {

    private final SalesUseCase salesUseCase;

    public SalesController(SalesUseCase salesUseCase) {
        this.salesUseCase = salesUseCase;
    }

    @GetMapping
    @Operation(summary = "売上一覧の取得", description = "すべての売上を取得します")
    @ApiResponse(responseCode = "200", description = "売上一覧を返却")
    public ResponseEntity<List<SalesResponse>> getAllSales(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) SalesStatus status,
            @Parameter(description = "顧客コードでフィルタ")
            @RequestParam(required = false) String customerCode,
            @Parameter(description = "受注IDでフィルタ")
            @RequestParam(required = false) Integer orderId) {

        List<Sales> sales;
        if (status != null) {
            sales = salesUseCase.getSalesByStatus(status);
        } else if (customerCode != null) {
            sales = salesUseCase.getSalesByCustomer(customerCode);
        } else if (orderId != null) {
            sales = salesUseCase.getSalesByOrder(orderId);
        } else {
            sales = salesUseCase.getAllSales();
        }

        List<SalesResponse> responses = sales.stream()
            .map(SalesResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{salesNumber}")
    @Operation(summary = "売上の取得", description = "売上番号を指定して売上情報を取得します")
    @ApiResponse(responseCode = "200", description = "売上を返却")
    @ApiResponse(responseCode = "404", description = "売上が見つからない")
    public ResponseEntity<SalesResponse> getSales(
            @Parameter(description = "売上番号", example = "SLS-20250101-0001")
            @PathVariable String salesNumber) {

        Sales sales = salesUseCase.getSalesByNumber(salesNumber);
        return ResponseEntity.ok(SalesResponse.from(sales));
    }

    @PostMapping
    @Operation(summary = "売上の登録", description = "新規売上を登録します")
    @ApiResponse(responseCode = "201", description = "売上を登録")
    public ResponseEntity<SalesResponse> createSales(
            @Valid @RequestBody CreateSalesRequest request) {

        List<CreateSalesCommand.CreateSalesDetailCommand> detailCommands = request.details().stream()
            .map(d -> new CreateSalesCommand.CreateSalesDetailCommand(
                d.orderDetailId(),
                d.shipmentDetailId(),
                d.productCode(),
                d.productName(),
                d.salesQuantity(),
                d.unit(),
                d.unitPrice(),
                d.remarks()
            ))
            .toList();

        CreateSalesCommand command = new CreateSalesCommand(
            request.salesDate(),
            request.orderId(),
            request.shipmentId(),
            request.customerCode(),
            request.customerBranchNumber(),
            request.representativeCode(),
            request.remarks(),
            detailCommands
        );

        Sales sales = salesUseCase.createSales(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SalesResponse.from(sales));
    }

    @PutMapping("/{salesNumber}")
    @Operation(summary = "売上の更新", description = "売上番号を指定して売上情報を更新します")
    @ApiResponse(responseCode = "200", description = "売上を更新")
    @ApiResponse(responseCode = "404", description = "売上が見つからない")
    public ResponseEntity<SalesResponse> updateSales(
            @Parameter(description = "売上番号", example = "SLS-20250101-0001")
            @PathVariable String salesNumber,
            @Valid @RequestBody UpdateSalesRequest request) {

        UpdateSalesCommand command = new UpdateSalesCommand(
            request.status(),
            request.billingId(),
            request.remarks()
        );

        Sales sales = salesUseCase.updateSales(salesNumber, command);
        return ResponseEntity.ok(SalesResponse.from(sales));
    }

    @PostMapping("/{salesNumber}/cancel")
    @Operation(summary = "売上のキャンセル", description = "売上番号を指定して売上をキャンセルします")
    @ApiResponse(responseCode = "200", description = "売上をキャンセル")
    @ApiResponse(responseCode = "404", description = "売上が見つからない")
    public ResponseEntity<SalesResponse> cancelSales(
            @Parameter(description = "売上番号", example = "SLS-20250101-0001")
            @PathVariable String salesNumber) {

        Sales sales = salesUseCase.cancelSales(salesNumber);
        return ResponseEntity.ok(SalesResponse.from(sales));
    }

    @DeleteMapping("/{salesNumber}")
    @Operation(summary = "売上の削除", description = "売上番号を指定して売上を削除します")
    @ApiResponse(responseCode = "204", description = "売上を削除")
    @ApiResponse(responseCode = "404", description = "売上が見つからない")
    public ResponseEntity<Void> deleteSales(
            @Parameter(description = "売上番号", example = "SLS-20250101-0001")
            @PathVariable String salesNumber) {

        salesUseCase.deleteSales(salesNumber);
        return ResponseEntity.noContent().build();
    }
}
