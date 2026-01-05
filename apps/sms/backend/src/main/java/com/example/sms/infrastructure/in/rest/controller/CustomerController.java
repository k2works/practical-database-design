package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.CustomerUseCase;
import com.example.sms.application.port.in.command.CreateCustomerCommand;
import com.example.sms.application.port.in.command.UpdateCustomerCommand;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.infrastructure.in.rest.dto.CreateCustomerRequest;
import com.example.sms.infrastructure.in.rest.dto.CustomerResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateCustomerRequest;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 顧客マスタ API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "customers", description = "顧客マスタ API")
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    public CustomerController(CustomerUseCase customerUseCase) {
        this.customerUseCase = customerUseCase;
    }

    @GetMapping
    @Operation(summary = "顧客一覧の取得", description = "すべての顧客を取得します")
    @ApiResponse(responseCode = "200", description = "顧客一覧を返却")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<Customer> customers = customerUseCase.getAllCustomers();
        List<CustomerResponse> responses = customers.stream()
            .map(CustomerResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{customerCode}")
    @Operation(summary = "顧客コードで顧客一覧を取得", description = "顧客コードを指定して顧客一覧（枝番違い含む）を取得します")
    @ApiResponse(responseCode = "200", description = "顧客一覧を返却")
    public ResponseEntity<List<CustomerResponse>> getCustomersByCode(
            @Parameter(description = "顧客コード", example = "CUS001")
            @PathVariable String customerCode) {

        List<Customer> customers = customerUseCase.getCustomersByCode(customerCode);
        List<CustomerResponse> responses = customers.stream()
            .map(CustomerResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{customerCode}/{branchNumber}")
    @Operation(summary = "顧客の取得", description = "顧客コードと枝番を指定して顧客情報を取得します")
    @ApiResponse(responseCode = "200", description = "顧客を返却")
    @ApiResponse(responseCode = "404", description = "顧客が見つからない")
    public ResponseEntity<CustomerResponse> getCustomer(
            @Parameter(description = "顧客コード", example = "CUS001")
            @PathVariable String customerCode,
            @Parameter(description = "顧客枝番", example = "00")
            @PathVariable String branchNumber) {

        Customer customer = customerUseCase.getCustomerByCodeAndBranch(customerCode, branchNumber);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @PostMapping
    @Operation(summary = "顧客の登録", description = "新規顧客を登録します")
    @ApiResponse(responseCode = "201", description = "顧客を登録")
    @ApiResponse(responseCode = "409", description = "顧客が既に存在する")
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        CreateCustomerCommand command = new CreateCustomerCommand(
            request.customerCode(),
            request.customerBranchNumber(),
            request.customerCategory(),
            request.billingCode(),
            request.billingBranchNumber(),
            request.collectionCode(),
            request.collectionBranchNumber(),
            request.customerName(),
            request.customerNameKana(),
            request.ourRepresentativeCode(),
            request.customerRepresentativeName(),
            request.customerDepartmentName(),
            request.customerPostalCode(),
            request.customerPrefecture(),
            request.customerAddress1(),
            request.customerAddress2(),
            request.customerPhone(),
            request.customerFax(),
            request.customerEmail(),
            request.billingType(),
            request.closingDay1(),
            request.paymentMonth1(),
            request.paymentDay1(),
            request.paymentMethod1(),
            request.closingDay2(),
            request.paymentMonth2(),
            request.paymentDay2(),
            request.paymentMethod2()
        );

        Customer customer = customerUseCase.createCustomer(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CustomerResponse.from(customer));
    }

    @PutMapping("/{customerCode}/{branchNumber}")
    @Operation(summary = "顧客の更新", description = "顧客コードと枝番を指定して顧客情報を更新します")
    @ApiResponse(responseCode = "200", description = "顧客を更新")
    @ApiResponse(responseCode = "404", description = "顧客が見つからない")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "顧客コード", example = "CUS001")
            @PathVariable String customerCode,
            @Parameter(description = "顧客枝番", example = "00")
            @PathVariable String branchNumber,
            @Valid @RequestBody UpdateCustomerRequest request) {

        UpdateCustomerCommand command = new UpdateCustomerCommand(
            request.customerCategory(),
            request.billingCode(),
            request.billingBranchNumber(),
            request.collectionCode(),
            request.collectionBranchNumber(),
            request.customerName(),
            request.customerNameKana(),
            request.ourRepresentativeCode(),
            request.customerRepresentativeName(),
            request.customerDepartmentName(),
            request.customerPostalCode(),
            request.customerPrefecture(),
            request.customerAddress1(),
            request.customerAddress2(),
            request.customerPhone(),
            request.customerFax(),
            request.customerEmail(),
            request.billingType(),
            request.closingDay1(),
            request.paymentMonth1(),
            request.paymentDay1(),
            request.paymentMethod1(),
            request.closingDay2(),
            request.paymentMonth2(),
            request.paymentDay2(),
            request.paymentMethod2()
        );

        Customer customer = customerUseCase.updateCustomer(customerCode, branchNumber, command);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @DeleteMapping("/{customerCode}/{branchNumber}")
    @Operation(summary = "顧客の削除", description = "顧客コードと枝番を指定して顧客を削除します")
    @ApiResponse(responseCode = "204", description = "顧客を削除")
    @ApiResponse(responseCode = "404", description = "顧客が見つからない")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "顧客コード", example = "CUS001")
            @PathVariable String customerCode,
            @Parameter(description = "顧客枝番", example = "00")
            @PathVariable String branchNumber) {

        customerUseCase.deleteCustomer(customerCode, branchNumber);
        return ResponseEntity.noContent().build();
    }
}
