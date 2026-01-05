package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.ReceiptUseCase;
import com.example.sms.application.port.in.command.CreateReceiptCommand;
import com.example.sms.application.port.in.command.UpdateReceiptCommand;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateReceiptRequest;
import com.example.sms.infrastructure.in.rest.dto.ReceiptResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateReceiptRequest;
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
 * 入金 API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/receipts")
@Tag(name = "receipts", description = "入金 API")
public class ReceiptController {

    private final ReceiptUseCase receiptUseCase;

    public ReceiptController(ReceiptUseCase receiptUseCase) {
        this.receiptUseCase = receiptUseCase;
    }

    @GetMapping
    @Operation(summary = "入金一覧の取得", description = "すべての入金を取得します")
    @ApiResponse(responseCode = "200", description = "入金一覧を返却")
    public ResponseEntity<List<ReceiptResponse>> getAllReceipts(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) ReceiptStatus status,
            @Parameter(description = "顧客コードでフィルタ")
            @RequestParam(required = false) String customerCode) {

        List<Receipt> receipts;
        if (status != null) {
            receipts = receiptUseCase.getReceiptsByStatus(status);
        } else if (customerCode != null) {
            receipts = receiptUseCase.getReceiptsByCustomer(customerCode);
        } else {
            receipts = receiptUseCase.getAllReceipts();
        }

        List<ReceiptResponse> responses = receipts.stream()
            .map(ReceiptResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{receiptNumber}")
    @Operation(summary = "入金の取得", description = "入金番号を指定して入金情報を取得します")
    @ApiResponse(responseCode = "200", description = "入金を返却")
    @ApiResponse(responseCode = "404", description = "入金が見つからない")
    public ResponseEntity<ReceiptResponse> getReceipt(
            @Parameter(description = "入金番号", example = "RCP-20250101-0001")
            @PathVariable String receiptNumber) {

        Receipt receipt = receiptUseCase.getReceiptByNumber(receiptNumber);
        return ResponseEntity.ok(ReceiptResponse.from(receipt));
    }

    @PostMapping
    @Operation(summary = "入金の登録", description = "新規入金を登録します")
    @ApiResponse(responseCode = "201", description = "入金を登録")
    public ResponseEntity<ReceiptResponse> createReceipt(
            @Valid @RequestBody CreateReceiptRequest request) {

        CreateReceiptCommand command = new CreateReceiptCommand(
            request.receiptDate(),
            request.customerCode(),
            request.customerBranchNumber(),
            request.receiptMethod(),
            request.receiptAmount(),
            request.bankFee(),
            request.payerName(),
            request.bankName(),
            request.accountNumber(),
            request.remarks()
        );

        Receipt receipt = receiptUseCase.createReceipt(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ReceiptResponse.from(receipt));
    }

    @PutMapping("/{receiptNumber}")
    @Operation(summary = "入金の更新", description = "入金番号を指定して入金情報を更新します")
    @ApiResponse(responseCode = "200", description = "入金を更新")
    @ApiResponse(responseCode = "404", description = "入金が見つからない")
    @ApiResponse(responseCode = "409", description = "楽観ロックエラー")
    public ResponseEntity<ReceiptResponse> updateReceipt(
            @Parameter(description = "入金番号", example = "RCP-20250101-0001")
            @PathVariable String receiptNumber,
            @Valid @RequestBody UpdateReceiptRequest request) {

        UpdateReceiptCommand command = new UpdateReceiptCommand(
            request.status(),
            request.remarks(),
            request.version()
        );

        Receipt receipt = receiptUseCase.updateReceipt(receiptNumber, command);
        return ResponseEntity.ok(ReceiptResponse.from(receipt));
    }

    @DeleteMapping("/{receiptNumber}")
    @Operation(summary = "入金の削除", description = "入金番号を指定して入金を削除します")
    @ApiResponse(responseCode = "204", description = "入金を削除")
    @ApiResponse(responseCode = "404", description = "入金が見つからない")
    public ResponseEntity<Void> deleteReceipt(
            @Parameter(description = "入金番号", example = "RCP-20250101-0001")
            @PathVariable String receiptNumber) {

        receiptUseCase.deleteReceipt(receiptNumber);
        return ResponseEntity.noContent().build();
    }
}
