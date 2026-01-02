package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.InvoiceUseCase;
import com.example.sms.application.port.in.command.CreateInvoiceCommand;
import com.example.sms.application.port.in.command.UpdateInvoiceCommand;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import com.example.sms.infrastructure.in.rest.dto.CreateInvoiceRequest;
import com.example.sms.infrastructure.in.rest.dto.InvoiceResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateInvoiceRequest;
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
 * 請求 API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "invoices", description = "請求 API")
public class InvoiceController {

    private final InvoiceUseCase invoiceUseCase;

    public InvoiceController(InvoiceUseCase invoiceUseCase) {
        this.invoiceUseCase = invoiceUseCase;
    }

    @GetMapping
    @Operation(summary = "請求一覧の取得", description = "すべての請求を取得します")
    @ApiResponse(responseCode = "200", description = "請求一覧を返却")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) InvoiceStatus status,
            @Parameter(description = "顧客コードでフィルタ")
            @RequestParam(required = false) String customerCode) {

        List<Invoice> invoices;
        if (status != null) {
            invoices = invoiceUseCase.getInvoicesByStatus(status);
        } else if (customerCode != null) {
            invoices = invoiceUseCase.getInvoicesByCustomer(customerCode);
        } else {
            invoices = invoiceUseCase.getAllInvoices();
        }

        List<InvoiceResponse> responses = invoices.stream()
            .map(InvoiceResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{invoiceNumber}")
    @Operation(summary = "請求の取得", description = "請求番号を指定して請求情報を取得します")
    @ApiResponse(responseCode = "200", description = "請求を返却")
    @ApiResponse(responseCode = "404", description = "請求が見つからない")
    public ResponseEntity<InvoiceResponse> getInvoice(
            @Parameter(description = "請求番号", example = "INV-20250101-0001")
            @PathVariable String invoiceNumber) {

        Invoice invoice = invoiceUseCase.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(InvoiceResponse.from(invoice));
    }

    @PostMapping
    @Operation(summary = "請求の登録", description = "新規請求を登録します")
    @ApiResponse(responseCode = "201", description = "請求を登録")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {

        CreateInvoiceCommand command = new CreateInvoiceCommand(
            request.invoiceDate(),
            request.customerCode(),
            request.customerBranchNumber(),
            request.closingDate(),
            request.previousBalance(),
            request.receiptAmount(),
            request.currentSalesAmount(),
            request.currentTaxAmount(),
            request.dueDate(),
            request.remarks()
        );

        Invoice invoice = invoiceUseCase.createInvoice(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(InvoiceResponse.from(invoice));
    }

    @PutMapping("/{invoiceNumber}")
    @Operation(summary = "請求の更新", description = "請求番号を指定して請求情報を更新します")
    @ApiResponse(responseCode = "200", description = "請求を更新")
    @ApiResponse(responseCode = "404", description = "請求が見つからない")
    @ApiResponse(responseCode = "409", description = "楽観ロックエラー")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @Parameter(description = "請求番号", example = "INV-20250101-0001")
            @PathVariable String invoiceNumber,
            @Valid @RequestBody UpdateInvoiceRequest request) {

        UpdateInvoiceCommand command = new UpdateInvoiceCommand(
            request.status(),
            request.remarks(),
            request.version()
        );

        Invoice invoice = invoiceUseCase.updateInvoice(invoiceNumber, command);
        return ResponseEntity.ok(InvoiceResponse.from(invoice));
    }

    @PostMapping("/{invoiceNumber}/issue")
    @Operation(summary = "請求の発行", description = "請求番号を指定して請求を発行します")
    @ApiResponse(responseCode = "200", description = "請求を発行")
    @ApiResponse(responseCode = "404", description = "請求が見つからない")
    @ApiResponse(responseCode = "409", description = "楽観ロックエラー")
    public ResponseEntity<InvoiceResponse> issueInvoice(
            @Parameter(description = "請求番号", example = "INV-20250101-0001")
            @PathVariable String invoiceNumber,
            @Parameter(description = "楽観ロック用バージョン")
            @RequestParam(required = false) Integer version) {

        Invoice invoice = invoiceUseCase.issueInvoice(invoiceNumber, version);
        return ResponseEntity.ok(InvoiceResponse.from(invoice));
    }

    @DeleteMapping("/{invoiceNumber}")
    @Operation(summary = "請求の削除", description = "請求番号を指定して請求を削除します")
    @ApiResponse(responseCode = "204", description = "請求を削除")
    @ApiResponse(responseCode = "404", description = "請求が見つからない")
    public ResponseEntity<Void> deleteInvoice(
            @Parameter(description = "請求番号", example = "INV-20250101-0001")
            @PathVariable String invoiceNumber) {

        invoiceUseCase.deleteInvoice(invoiceNumber);
        return ResponseEntity.noContent().build();
    }
}
